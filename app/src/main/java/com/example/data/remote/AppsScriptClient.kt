package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AppsScriptClient(private val getBaseUrl: () -> String) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun getCleanUrl(): String {
        return cleanUrl(getBaseUrl())
    }

    suspend fun callAction(action: String, token: String? = null, params: Map<String, Any?> = emptyMap()): Result<JSONObject> {
        return withContext(Dispatchers.IO) {
            val url = cleanUrl(getBaseUrl())
            if (url.isBlank()) {
                return@withContext Result.failure(IllegalStateException("Google Apps Script Web App URL is not set. Please set it in App Settings."))
            }

            try {
                // Prepare JSON payload
                val jsonPayload = JSONObject().apply {
                    put("action", action)
                    if (!token.isNullOrBlank()) {
                        put("token", token)
                    }
                    for ((k, v) in params) {
                        when (v) {
                            null -> put(k, JSONObject.NULL)
                            is Map<*, *> -> put(k, JSONObject(v))
                            is List<*> -> put(k, JSONArray(v))
                            else -> put(k, v)
                        }
                    }
                }

                // Append ?action=... to URL so both query-param based doPost(e) and body-based doPost(e) receive it
                val targetUrl = if (url.contains("?")) "$url&action=$action" else "$url?action=$action"
                val requestBody = jsonPayload.toString().toRequestBody(jsonMediaType)
                val request = Request.Builder()
                    .url(targetUrl)
                    .post(requestBody)
                    .header("Accept", "application/json, text/plain, */*")
                    .build()

                var response = client.newCall(request).execute()
                var bodyString = response.body?.string()?.trim() ?: ""

                // Check if POST returned "Script function not found: doPost" - if so, attempt GET fallback
                if (bodyString.contains("Script function not found: doPost") || response.code == 405) {
                    response.close()
                    val getUrlBuilder = url.toHttpUrlOrNull()?.newBuilder()
                    if (getUrlBuilder != null) {
                        getUrlBuilder.addQueryParameter("action", action)
                        if (!token.isNullOrBlank()) {
                            getUrlBuilder.addQueryParameter("token", token)
                        }
                        for ((k, v) in params) {
                            if (v != null) {
                                getUrlBuilder.addQueryParameter(k, v.toString())
                            }
                        }
                        val getReq = Request.Builder().url(getUrlBuilder.build()).get().build()
                        response = client.newCall(getReq).execute()
                        bodyString = response.body?.string()?.trim() ?: ""
                    }
                }

                response.use {
                    if (bodyString.isBlank()) {
                        return@withContext Result.failure(Exception("Unable to connect to Apps Script."))
                    }

                    // Check for Google account authentication requirement (happens when Web App access is not "Anyone")
                    if (bodyString.contains("accounts.google.com") || bodyString.contains("Sign in - Google Accounts")) {
                        return@withContext Result.failure(Exception("Apps Script permission error: Set 'Who has access' to 'Anyone' in Web App deployment."))
                    }

                    // Check for HTML error output from Google Apps Script
                    if (bodyString.startsWith("<!DOCTYPE", ignoreCase = true) || bodyString.startsWith("<html", ignoreCase = true)) {
                        if (bodyString.contains("Script function not found: doPost")) {
                            return@withContext Result.failure(Exception("Apps Script is missing doPost function in Code.gs."))
                        }
                        return@withContext Result.failure(Exception("Unable to connect to Apps Script. Please verify the Web App deployment URL."))
                    }

                    // Flexible parsing: JSON object, JSON array, JSON string, or plain string
                    val json: JSONObject = try {
                        when {
                            bodyString.startsWith("{") -> JSONObject(bodyString)
                            bodyString.startsWith("[") -> JSONObject().apply {
                                put("ok", true)
                                put("rows", JSONArray(bodyString))
                            }
                            bodyString.equals("true", ignoreCase = true) -> JSONObject().apply {
                                put("ok", true)
                            }
                            bodyString.startsWith("\"") && bodyString.endsWith("\"") -> {
                                val text = bodyString.removeSurrounding("\"")
                                JSONObject().apply {
                                    put("ok", true)
                                    put("message", text)
                                }
                            }
                            !bodyString.startsWith("<") -> {
                                JSONObject().apply {
                                    put("ok", true)
                                    put("message", bodyString)
                                }
                            }
                            else -> throw JSONException("Invalid format")
                        }
                    } catch (e: Exception) {
                        return@withContext Result.failure(Exception("Unable to connect to Apps Script. Please verify Web App URL."))
                    }

                    // Check for explicit error field
                    if (json.has("error") && !json.isNull("error")) {
                        val error = json.optString("error", "Action failed")
                        return@withContext Result.failure(Exception(cleanErrorMessage(error)))
                    }

                    val isOk = json.optBoolean("ok", true)
                    if (isOk || json.has("status") || json.has("rows") || json.has("total") || json.has("users") || json.has("token")) {
                        Result.success(json)
                    } else {
                        val error = json.optString("error", "Action failed")
                        Result.failure(Exception(cleanErrorMessage(error)))
                    }
                }
            } catch (e: Exception) {
                Result.failure(Exception(cleanErrorMessage(e.message ?: "Connection failed")))
            }
        }
    }

    suspend fun testConnection(): Result<String> {
        return withContext(Dispatchers.IO) {
            val url = cleanUrl(getBaseUrl())
            if (url.isBlank()) {
                return@withContext Result.failure(IllegalStateException("Please enter your Google Apps Script Web App URL."))
            }

            try {
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    val body = response.body?.string() ?: ""
                    if (body.contains("accounts.google.com")) {
                        Result.failure(Exception("Permission required: Please set 'Who has access' to 'Anyone' in Apps Script deployment."))
                    } else if (response.isSuccessful || response.code == 302) {
                        Result.success("Connected successfully!")
                    } else {
                        Result.failure(Exception("Connection failed (HTTP ${response.code})"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(Exception(cleanErrorMessage(e.message)))
            }
        }
    }

    companion object {
        fun cleanUrl(raw: String): String {
            var url = raw.trim()
            if (url.isBlank()) return ""
            // Replace /edit or /edit#... with /exec
            if (url.contains("/edit")) {
                url = url.substringBefore("/edit") + "/exec"
            }
            while (url.endsWith("/")) {
                url = url.dropLast(1)
            }
            return url
        }

        fun cleanErrorMessage(raw: String?): String {
            if (raw.isNullOrBlank()) return "Connection failed. Please check your network."
            val lower = raw.lowercase()
            return when {
                lower.contains("not authorised") || lower.contains("not authorized") ->
                    "This email is not authorised."
                lower.contains("code expired") ->
                    "Code expired. Please request a new one."
                lower.contains("incorrect code") ->
                    "Incorrect code."
                lower.contains("valid email") ->
                    "Please enter a valid email."
                lower.contains("dopost") ->
                    "Apps Script is missing doPost function in Code.gs."
                lower.contains("permission") || lower.contains("who has access") || lower.contains("accounts.google") ->
                    "Apps Script permission error: Set 'Who has access' to 'Anyone'."
                lower.contains("doctype") || lower.contains("invalid json") || lower.contains("html") ->
                    "Unable to connect to Apps Script. Please verify Web App URL."
                lower.contains("timeout") || lower.contains("timed out") ->
                    "Request timed out. Please try again."
                else -> raw.substringBefore("\n").take(80)
            }
        }

        const val CODE_GS_DOPROST_SNIPPET = """
function doPost(e) {
  try {
    var body = e.postData ? e.postData.contents : '';
    var req = body ? JSON.parse(body) : {};
    var action = req.action || (e.parameter ? e.parameter.action : '');
    var token = req.token || (e.parameter ? e.parameter.token : '');
    var res = {};

    switch(action) {
      case 'requestLoginCode':
        res = requestLoginCode(req.email || (e.parameter ? e.parameter.email : ''));
        break;
      case 'verifyLoginCode':
        res = verifyLoginCode(req.email || (e.parameter ? e.parameter.email : ''), req.code || (e.parameter ? e.parameter.code : ''));
        break;
      case 'resumeSession':
        res = resumeSession(token);
        break;
      case 'logout':
        res = logout(token);
        break;
      case 'getDashboard':
        res = getDashboard(token, req.opts || {});
        break;
      case 'listRecurring':
        res = listRecurring(token, req.opts || {});
        break;
      case 'listOneTime':
        res = listOneTime(token, req.opts || {});
        break;
      case 'assignRecurringTask':
        res = assignRecurringTask(token, req.data || {});
        break;
      case 'assignOneTimeTask':
        res = assignOneTimeTask(token, req.data || {});
        break;
      case 'completeRecurring':
        res = completeRecurring(token, req.rowNum, req.payload || {});
        break;
      case 'completeOneTime':
        res = completeOneTime(token, req.rowNum, req.payload || {});
        break;
      case 'editRecurring':
        res = editRecurring(token, req.rowNum, req.sno, req.data || {});
        break;
      case 'editOneTimeTask':
        res = editOneTimeTask(token, req.rowNum, req.taskId, req.data || {});
        break;
      case 'deleteRecurringSeries':
        res = deleteRecurringSeries(token, req.rowNum, req.sno);
        break;
      case 'deleteOneTimeTask':
        res = deleteOneTimeTask(token, req.rowNum, req.taskId);
        break;
      case 'listUsers':
        res = { ok: true, users: listUsers(token) };
        break;
      case 'addUser':
        res = addUser(token, req.user || {});
        break;
      case 'updateUserRole':
        res = updateUserRole(token, req.rowNum, req.role);
        break;
      case 'deleteUser':
        res = deleteUser(token, req.rowNum);
        break;
      case 'getSettings':
        res = { ok: true, settings: getSettings(token) };
        break;
      case 'saveSettings':
        res = saveSettings(token, req.settings || {});
        break;
      default:
        res = { ok: false, error: 'Unknown action: ' + action };
    }

    return ContentService.createTextOutput(JSON.stringify(res))
      .setMimeType(ContentService.MimeType.JSON);
  } catch(err) {
    return ContentService.createTextOutput(JSON.stringify({ ok: false, error: err.message || String(err) }))
      .setMimeType(ContentService.MimeType.JSON);
  }
}
"""
    }
}
