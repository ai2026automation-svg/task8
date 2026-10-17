package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.LineBorder
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusDelay
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun LoginScreen(
    appsScriptUrl: String,
    email: String,
    code: String,
    step: Int, // 0 = URL, 1 = Email, 2 = Code
    msg: String,
    isError: Boolean,
    isLoading: Boolean,
    onUrlSubmit: (String) -> Unit,
    onBackToUrl: () -> Unit,
    onEmailChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onRequestCode: () -> Unit,
    onVerifyCode: () -> Unit,
    onBackToEmail: () -> Unit,
    onOpenServerConfig: () -> Unit
) {
    var urlInput by remember(appsScriptUrl) { mutableStateOf(appsScriptUrl) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 380.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Boot Logo (SMARTFIN CFO)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "SMARTFIN",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = NavyPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "CFO",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = OrangePrimary,
                        letterSpacing = 0.5.sp
                    )
                }

                // Title: <h2>Task Manager</h2>
                Text(
                    text = "Task Manager",
                    fontSize = 22.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // STEP 0: WEB APP URL
                if (step == 0) {
                    HtmlStyleTextInput(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        placeholder = "https://script.google.com/macros/s/.../exec",
                        keyboardType = KeyboardType.Uri,
                        onDone = {
                            if (urlInput.isNotBlank()) onUrlSubmit(urlInput.trim())
                        },
                        testTag = "login_url"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onUrlSubmit(urlInput.trim()) },
                        enabled = !isLoading && urlInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_save_url")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Continue", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // STEP 1: EMAIL
                else if (step == 1) {
                    HtmlStyleTextInput(
                        value = email,
                        onValueChange = onEmailChange,
                        placeholder = "you@company.com",
                        keyboardType = KeyboardType.Email,
                        onDone = { onRequestCode() },
                        testTag = "login-email"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onRequestCode,
                        enabled = !isLoading && email.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn-send-code")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Send code", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "← Change Web App URL",
                        color = OrangePrimary,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clickable { onBackToUrl() }
                            .padding(4.dp)
                    )
                }

                // STEP 2: 6-DIGIT CODE
                else {
                    Row(
                        modifier = Modifier.padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Code sent to ",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        Text(
                            text = email,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }

                    HtmlStyleCodeInput(
                        value = code,
                        onValueChange = { if (it.length <= 6) onCodeChange(it.filter { c -> c.isDigit() }) },
                        onDone = { onVerifyCode() },
                        testTag = "login-code"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onVerifyCode,
                        enabled = !isLoading && code.length == 6,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn-verify")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Verify & sign in", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "← Use a different email",
                        color = OrangePrimary,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clickable { onBackToEmail() }
                            .padding(4.dp)
                    )
                }

                // Message: <p id="login-msg" class="login-msg">
                if (msg.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = msg,
                        color = if (isError) StatusDelay else NavyPrimary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("login-msg")
                    )
                }
            }
        }
    }
}

@Composable
private fun HtmlStyleTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    onDone: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.5.dp, if (value.isNotEmpty()) OrangePrimary else LineBorder, RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                color = Color(0xFF94A3B8),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = TextDark,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            cursorBrush = SolidColor(OrangePrimary),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}

@Composable
private fun HtmlStyleCodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.5.dp, if (value.isNotEmpty()) OrangePrimary else LineBorder, RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        if (value.isEmpty()) {
            Text(
                text = "------",
                color = Color(0xFFCBD5E1),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 10.sp,
                textAlign = TextAlign.Center
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = NavyPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 10.sp,
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            cursorBrush = SolidColor(OrangePrimary),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}
