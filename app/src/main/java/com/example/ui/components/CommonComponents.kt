package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskStatus
import com.example.data.model.User
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LineBorder
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.NavyTertiary
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.PriorityHighBg
import com.example.ui.theme.PriorityHighText
import com.example.ui.theme.PriorityLowBg
import com.example.ui.theme.PriorityLowText
import com.example.ui.theme.PriorityMediumBg
import com.example.ui.theme.PriorityMediumText
import com.example.ui.theme.StatusDelay
import com.example.ui.theme.StatusOk
import com.example.ui.theme.StatusPending
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

/**
 * High-contrast, fully opaque text field colors ensuring typed text is 100% visible,
 * crisp, and readable on all screens and devices.
 */
@Composable
fun appOutlinedTextFieldColors(
    containerColor: Color = Color.White
): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextDark,
    unfocusedTextColor = TextDark,
    disabledTextColor = TextDark.copy(alpha = 0.7f),
    errorTextColor = Color(0xFFDC2626),
    focusedContainerColor = containerColor,
    unfocusedContainerColor = containerColor,
    disabledContainerColor = Color(0xFFF3F4F6),
    cursorColor = OrangePrimary,
    focusedBorderColor = OrangePrimary,
    unfocusedBorderColor = LineBorder,
    focusedLabelColor = OrangePrimary,
    unfocusedLabelColor = Color(0xFF4B5563),
    disabledLabelColor = TextMuted,
    focusedPlaceholderColor = Color(0xFF9CA3AF),
    unfocusedPlaceholderColor = Color(0xFF9CA3AF),
    focusedSupportingTextColor = TextMuted,
    unfocusedSupportingTextColor = TextMuted
)

@Composable
fun TopBar(
    currentUser: User?,
    isAppsScriptConnected: Boolean,
    onOpenProfile: () -> Unit,
    onOpenServerConfig: () -> Unit
) {
    Surface(
        color = NavyPrimary,
        shadowElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Sleek, compact Brand Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White)
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SMARTFIN",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.5.sp,
                            color = NavyPrimary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "CFO",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.5.sp,
                            color = OrangePrimary,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Sleek Profile Option Button
                if (currentUser != null) {
                    Surface(
                        onClick = onOpenProfile,
                        shape = RoundedCornerShape(20.dp),
                        color = NavySecondary,
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
                        modifier = Modifier.testTag("btn_profile")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 4.dp, end = 10.dp, top = 3.dp, bottom = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(OrangePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser.name.take(1).uppercase(),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(
                                text = "Profile",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Server connectivity status dot
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isAppsScriptConnected) Color(0xFF4CAF50) else GoldAccent)
                            )
                        }
                    }
                } else {
                    IconButton(
                        onClick = onOpenServerConfig,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_server_config")
                    ) {
                        Icon(
                            imageVector = if (isAppsScriptConnected) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = "Server Connection",
                            tint = if (isAppsScriptConnected) Color(0xFF4CAF50) else GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Subtle Gold accent line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(GoldAccent)
            )
        }
    }
}

@Composable
fun ProfileDialog(
    user: User,
    isAppsScriptConnected: Boolean,
    onOpenServerConfig: () -> Unit,
    onLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = NavyPrimary,
            border = BorderStroke(2.dp, GoldAccent.copy(alpha = 0.8f)),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .testTag("dialog_profile")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Profile",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar Circle
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(OrangePrimary)
                        .border(2.dp, GoldAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // User Full Name
                Text(
                    text = user.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Role Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GoldAccent)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = user.role.name.replace("_", " "),
                        color = NavyPrimary,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }

                if (user.email.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = user.email,
                        fontSize = 12.sp,
                        color = Color(0xFFC7D2FE)
                    )
                }

                if (user.department.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Department: ${user.department}",
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Server Status Card
                Surface(
                    color = NavySecondary,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, LineBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isAppsScriptConnected) Icons.Default.CloudDone else Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = if (isAppsScriptConnected) Color(0xFF4CAF50) else GoldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Google Apps Script",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isAppsScriptConnected) "Connected to server" else "URL not configured",
                                    fontSize = 10.5.sp,
                                    color = if (isAppsScriptConnected) Color(0xFF81C784) else GoldAccent
                                )
                            }
                        }

                        IconButton(
                            onClick = onOpenServerConfig,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("btn_server_config")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configure Server",
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions: Sign Out button
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_logout")
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign Out",
                        color = Color.White,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: TaskStatus) {
    val (bgColor, textColor) = when (status) {
        TaskStatus.ON_TIME -> StatusOk to Color.White
        TaskStatus.DELAY -> StatusDelay to Color.White
        TaskStatus.PENDING -> StatusPending to NavyPrimary
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.displayName,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
        )
    }
}

@Composable
fun PriorityBadge(priority: String) {
    val p = priority.trim().lowercase()
    val (bgColor, textColor) = when {
        p.contains("high") -> PriorityHighBg to PriorityHighText
        p.contains("low") -> PriorityLowBg to PriorityLowText
        else -> PriorityMediumBg to PriorityMediumText
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(horizontal = 7.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = priority,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun KpiCard(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
        shadowElevation = 1.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(accentColor)
            )
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (accentColor != NavyPrimary) accentColor else NavyPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun DoughnutStatusChart(
    onTime: Int,
    delay: Int,
    pending: Int,
    modifier: Modifier = Modifier
) {
    val total = (onTime + delay + pending).coerceAtLeast(1)
    val onTimeSweep = (onTime.toFloat() / total) * 360f
    val delaySweep = (delay.toFloat() / total) * 360f
    val pendingSweep = (pending.toFloat() / total) * 360f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Status Split",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NavyPrimary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(130.dp)) {
                var currentAngle = -90f
                val strokeWidth = 24.dp.toPx()
                val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                if (onTime > 0) {
                    drawArc(
                        color = StatusOk,
                        startAngle = currentAngle,
                        sweepAngle = onTimeSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                    currentAngle += onTimeSweep
                }
                if (delay > 0) {
                    drawArc(
                        color = StatusDelay,
                        startAngle = currentAngle,
                        sweepAngle = delaySweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                    currentAngle += delaySweep
                }
                if (pending > 0) {
                    drawArc(
                        color = StatusPending,
                        startAngle = currentAngle,
                        sweepAngle = pendingSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = (onTime + delay + pending).toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Text(
                    text = "Total",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ChartLegendItem(color = StatusOk, label = "On Time", count = onTime)
            ChartLegendItem(color = StatusDelay, label = "Delay", count = delay)
            ChartLegendItem(color = StatusPending, label = "Pending", count = pending)
        }
    }
}

@Composable
private fun ChartLegendItem(color: Color, label: String, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$label ($count)",
            fontSize = 11.sp,
            color = TextDark,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SimpleBarChart(
    title: String,
    data: Map<String, Int>,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    val maxVal = data.values.maxOrNull()?.coerceAtLeast(1) ?: 1

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NavyPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (data.isEmpty()) {
            Text(
                text = "No data available",
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                data.forEach { (label, count) ->
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = TextDark,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = count.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFE9ECEF))
                        ) {
                            val fraction = (count.toFloat() / maxVal).coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(barColor)
                            )
                        }
                    }
                }
            }
        }
    }
}
