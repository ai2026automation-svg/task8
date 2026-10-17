package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.ReminderSettings

class TaskPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("smartfin_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_APPS_SCRIPT_URL = "apps_script_url"
        private const val KEY_SESSION_TOKEN = "session_token"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_DEPT = "user_dept"
        private const val KEY_USER_DESIGNATION = "user_designation"

        private const val KEY_NOTIFY_ASSIGN = "notify_assign"
        private const val KEY_NOTIFY_COMPLETE = "notify_complete"
        private const val KEY_PENDING_REMINDERS = "pending_reminders"
        private const val KEY_REMINDER_HOUR = "reminder_hour"
        private const val KEY_REMINDER_EMAILS = "reminder_emails"
    }

    var appsScriptUrl: String
        get() = prefs.getString(KEY_APPS_SCRIPT_URL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_APPS_SCRIPT_URL, value.trim()).apply()

    var sessionToken: String
        get() = prefs.getString(KEY_SESSION_TOKEN, "") ?: ""
        set(value) = prefs.edit().putString(KEY_SESSION_TOKEN, value).apply()

    var userEmail: String
        get() = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_EMAIL, value).apply()

    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    var userRole: String
        get() = prefs.getString(KEY_USER_ROLE, "USER") ?: "USER"
        set(value) = prefs.edit().putString(KEY_USER_ROLE, value).apply()

    var userDepartment: String
        get() = prefs.getString(KEY_USER_DEPT, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_DEPT, value).apply()

    var userDesignation: String
        get() = prefs.getString(KEY_USER_DESIGNATION, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_DESIGNATION, value).apply()

    fun saveUserSession(token: String, email: String, name: String, role: String, dept: String, designation: String) {
        prefs.edit()
            .putString(KEY_SESSION_TOKEN, token)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_ROLE, role)
            .putString(KEY_USER_DEPT, dept)
            .putString(KEY_USER_DESIGNATION, designation)
            .apply()
    }

    fun clearSession() {
        prefs.edit()
            .remove(KEY_SESSION_TOKEN)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_ROLE)
            .remove(KEY_USER_DEPT)
            .remove(KEY_USER_DESIGNATION)
            .apply()
    }

    fun getSettings(): ReminderSettings {
        return ReminderSettings(
            notifyOnAssign = prefs.getBoolean(KEY_NOTIFY_ASSIGN, true),
            notifyOnComplete = prefs.getBoolean(KEY_NOTIFY_COMPLETE, true),
            pendingReminders = prefs.getBoolean(KEY_PENDING_REMINDERS, false),
            reminderHour = prefs.getInt(KEY_REMINDER_HOUR, 9),
            reminderEmails = prefs.getString(KEY_REMINDER_EMAILS, "") ?: ""
        )
    }

    fun saveSettings(settings: ReminderSettings) {
        prefs.edit()
            .putBoolean(KEY_NOTIFY_ASSIGN, settings.notifyOnAssign)
            .putBoolean(KEY_NOTIFY_COMPLETE, settings.notifyOnComplete)
            .putBoolean(KEY_PENDING_REMINDERS, settings.pendingReminders)
            .putInt(KEY_REMINDER_HOUR, settings.reminderHour)
            .putString(KEY_REMINDER_EMAILS, settings.reminderEmails)
            .apply()
    }
}
