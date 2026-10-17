package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReminderSettings
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.ui.components.appOutlinedTextFieldColors
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LineBorder
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusDelay
import com.example.ui.theme.StatusOk
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UsersTab(
    users: List<User>,
    name: String,
    designation: String,
    email: String,
    department: String,
    role: UserRole,
    msg: String,
    isError: Boolean,
    settings: ReminderSettings,
    settingsMsg: String,
    onUserFormChange: (name: String?, desig: String?, email: String?, dept: String?, role: UserRole?) -> Unit,
    onSubmitUser: () -> Unit,
    onChangeRole: (rowNum: Int, role: UserRole) -> Unit,
    onDeleteUser: (rowNum: Int) -> Unit,
    onUpdateSettings: (notifyAssign: Boolean?, notifyComp: Boolean?, pendingRem: Boolean?, hour: Int?, emails: String?) -> Unit,
    onSaveSettings: () -> Unit
) {
    var roleExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "User Management",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = NavyPrimary
        )

        // Add User Form
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Add New User",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { onUserFormChange(it, null, null, null, null) },
                        label = { Text("Name") },
                        colors = appOutlinedTextFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = designation,
                        onValueChange = { onUserFormChange(null, it, null, null, null) },
                        label = { Text("Designation") },
                        colors = appOutlinedTextFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { onUserFormChange(null, null, it, null, null) },
                        label = { Text("Email Address *") },
                        colors = appOutlinedTextFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = department,
                        onValueChange = { onUserFormChange(null, null, null, it, null) },
                        label = { Text("Department") },
                        placeholder = { Text("e.g. Accounts, Audit") },
                        colors = appOutlinedTextFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = roleExpanded,
                    onExpandedChange = { roleExpanded = it }
                ) {
                    OutlinedTextField(
                        value = role.name.lowercase().replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Role") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                        colors = appOutlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = roleExpanded,
                        onDismissRequest = { roleExpanded = false }
                    ) {
                        UserRole.values().forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    roleExpanded = false
                                    onUserFormChange(null, null, null, null, r)
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onSubmitUser,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("btn_add_user")
                    ) {
                        Text("Add User", fontWeight = FontWeight.Bold)
                    }

                    if (msg.isNotBlank()) {
                        Text(
                            text = msg,
                            fontSize = 12.sp,
                            color = if (isError) StatusDelay else StatusOk,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = "Admin — sees and assigns everything, edits/deletes any task, manages users. Moderator — sees and assigns only within their own Department. User — sees only their own tasks and can assign one-time tasks to themselves.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp
                )
            }
        }

        // Users List Table
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "All Users (${users.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .background(NavySecondary)
                                .padding(vertical = 8.dp, horizontal = 10.dp)
                        ) {
                            TableCell("Name", 130.dp, isHeader = true)
                            TableCell("Designation", 130.dp, isHeader = true)
                            TableCell("Email", 180.dp, isHeader = true)
                            TableCell("Department", 120.dp, isHeader = true)
                            TableCell("Role", 110.dp, isHeader = true)
                            TableCell("Actions", 80.dp, isHeader = true)
                        }

                        users.forEach { u ->
                            var itemRoleExpanded by remember { mutableStateOf(false) }

                            Row(
                                modifier = Modifier
                                    .border(0.5.dp, LineBorder)
                                    .padding(vertical = 6.dp, horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell(u.name, 130.dp, isBold = true)
                                TableCell(u.designation, 130.dp)
                                TableCell(u.email, 180.dp)
                                TableCell(if (u.department.isNotBlank()) u.department else "—", 120.dp)

                                // Role selector
                                Box(modifier = Modifier.width(110.dp)) {
                                    ExposedDropdownMenuBox(
                                        expanded = itemRoleExpanded,
                                        onExpandedChange = { itemRoleExpanded = it }
                                    ) {
                                        Text(
                                            text = u.role.name.lowercase().replaceFirstChar { it.uppercase() } + " ▼",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OrangePrimary,
                                            modifier = Modifier
                                                .menuAnchor()
                                                .clickable { itemRoleExpanded = true }
                                                .padding(vertical = 4.dp)
                                        )
                                        ExposedDropdownMenu(
                                            expanded = itemRoleExpanded,
                                            onDismissRequest = { itemRoleExpanded = false }
                                        ) {
                                            UserRole.values().forEach { r ->
                                                DropdownMenuItem(
                                                    text = { Text(r.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 12.sp) },
                                                    onClick = {
                                                        itemRoleExpanded = false
                                                        onChangeRole(u.row, r)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                Box(modifier = Modifier.width(80.dp)) {
                                    IconButton(
                                        onClick = { onDeleteUser(u.row) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = StatusDelay, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Email Reminders & Notifications Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Email Reminders & Notifications",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )

                // Checkbox 1
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = settings.notifyOnAssign,
                        onCheckedChange = { onUpdateSettings(it, null, null, null, null) },
                        colors = CheckboxDefaults.colors(checkedColor = OrangePrimary)
                    )
                    Text(
                        text = "Email the doer when a one-time task is assigned",
                        fontSize = 13.sp,
                        color = TextDark
                    )
                }

                // Checkbox 2
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = settings.notifyOnComplete,
                        onCheckedChange = { onUpdateSettings(null, it, null, null, null) },
                        colors = CheckboxDefaults.colors(checkedColor = OrangePrimary)
                    )
                    Text(
                        text = "Email the assigner when their one-time task is completed",
                        fontSize = 13.sp,
                        color = TextDark
                    )
                }

                // Checkbox 3
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = settings.pendingReminders,
                        onCheckedChange = { onUpdateSettings(null, null, it, null, null) },
                        colors = CheckboxDefaults.colors(checkedColor = OrangePrimary)
                    )
                    Text(
                        text = "Send a daily reminder digest of pending / overdue tasks",
                        fontSize = 13.sp,
                        color = TextDark
                    )
                }

                // Hour & Emails
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = settings.reminderHour.toString(),
                        onValueChange = {
                            val h = it.filter { c -> c.isDigit() }.toIntOrNull() ?: 9
                            onUpdateSettings(null, null, null, h.coerceIn(0, 23), null)
                        },
                        label = { Text("Reminder hour (0-23)") },
                        colors = appOutlinedTextFieldColors(),
                        modifier = Modifier.width(160.dp)
                    )

                    OutlinedTextField(
                        value = settings.reminderEmails,
                        onValueChange = { onUpdateSettings(null, null, null, null, it) },
                        label = { Text("Admin digest recipients") },
                        placeholder = { Text("manager@company.com, cfo@company.com") },
                        colors = appOutlinedTextFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onSaveSettings,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("btn_save_settings")
                    ) {
                        Text("Save Settings", fontWeight = FontWeight.Bold)
                    }

                    if (settingsMsg.isNotBlank()) {
                        Text(
                            text = settingsMsg,
                            fontSize = 12.sp,
                            color = StatusOk,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = "Reminders are sent automatically once per day at the chosen hour. Each doer gets only their own pending tasks; the admin recipients get the full team digest.",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isHeader: Boolean = false,
    isBold: Boolean = false
) {
    Text(
        text = text,
        fontSize = if (isHeader) 11.sp else 11.5.sp,
        fontWeight = if (isHeader || isBold) FontWeight.Bold else FontWeight.Normal,
        color = if (isHeader) Color.White else TextDark,
        modifier = Modifier.width(width)
    )
}
