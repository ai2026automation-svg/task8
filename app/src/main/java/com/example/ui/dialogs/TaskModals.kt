package com.example.ui.dialogs

import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.TaskItem
import com.example.data.model.TaskType
import com.example.data.model.User
import com.example.data.remote.AppsScriptClient
import com.example.logic.DateUtils
import com.example.ui.components.appOutlinedTextFieldColors
import com.example.ui.theme.LineBorder
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusDelay
import com.example.ui.theme.StatusOk
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import java.util.Calendar
import java.util.Date

@Composable
fun CompleteTaskDialog(
    task: TaskItem,
    onDismiss: () -> Unit,
    onSave: (remarks: String, attachFile: String) -> Unit
) {
    var remarks by remember { mutableStateOf(task.remarks) }
    var attachFile by remember { mutableStateOf(task.attachFile) }
    val nowFormatted = remember { DateUtils.formatTimestamp(Date()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                // Modal Head
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavyPrimary)
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Update Task",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Modal Body
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = task.task,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )

                    // Completed timestamp (read-only)
                    OutlinedTextField(
                        value = nowFormatted,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Completed Date & Time") },
                        supportingText = { Text("Set automatically when you save — cannot be changed.") },
                        colors = appOutlinedTextFieldColors(containerColor = Color(0xFFF3F4F6)),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Remarks
                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        label = { Text("Remarks") },
                        placeholder = { Text("Enter completion notes, remarks, or summary…") },
                        minLines = 3,
                        colors = appOutlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_complete_remarks")
                    )

                    // File attachment / link
                    OutlinedTextField(
                        value = attachFile,
                        onValueChange = { attachFile = it },
                        label = { Text("Attach File / Link") },
                        placeholder = { Text("https://... or link to completed file") },
                        colors = appOutlinedTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Foot Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAFAFA))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel", color = NavyPrimary)
                    }

                    Button(
                        onClick = { onSave(remarks, attachFile) },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_save_complete")
                    ) {
                        Text("Save & Complete", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    task: TaskItem,
    scopedDoers: List<User>,
    onDismiss: () -> Unit,
    onSave: (title: String, doer: String, priority: String, dueIso: String, attachFile: String, applyToSeries: Boolean) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(task.task) }
    var doer by remember { mutableStateOf(task.doerName) }
    var priority by remember { mutableStateOf(task.priority) }
    var dueDateIso by remember {
        val parsed = DateUtils.parseDate(task.dueDate)
        mutableStateOf(if (parsed != null) DateUtils.formatIso(parsed) else "")
    }
    var attachFile by remember { mutableStateOf(task.assignerFile) }
    var applyToSeries by remember { mutableStateOf(true) }

    var doerExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavyPrimary)
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (task.type == TaskType.RECURRING) "Edit Recurring Task" else "Edit One-Time Task",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Title *") },
                        colors = appOutlinedTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Doer Dropdown
                    ExposedDropdownMenuBox(
                        expanded = doerExpanded,
                        onExpandedChange = { doerExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = doer,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Assignee / Doer *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = doerExpanded) },
                            colors = appOutlinedTextFieldColors(),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = doerExpanded,
                            onDismissRequest = { doerExpanded = false }
                        ) {
                            scopedDoers.forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d.name) },
                                    onClick = {
                                        doer = d.name
                                        doerExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (task.type == TaskType.ONE_TIME) {
                        // Priority
                        ExposedDropdownMenuBox(
                            expanded = priorityExpanded,
                            onExpandedChange = { priorityExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = priority,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Priority") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                                colors = appOutlinedTextFieldColors(),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = priorityExpanded,
                                onDismissRequest = { priorityExpanded = false }
                            ) {
                                listOf("High", "Medium", "Low").forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text(p) },
                                        onClick = {
                                            priority = p
                                            priorityExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Attach file
                        OutlinedTextField(
                            value = attachFile,
                            onValueChange = { attachFile = it },
                            label = { Text("Reference File / Link") },
                            colors = appOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Due Date Picker
                    OutlinedTextField(
                        value = dueDateIso,
                        onValueChange = { dueDateIso = it },
                        label = { Text(if (task.type == TaskType.RECURRING) "Due Date (this occurrence only)" else "Due Date *") },
                        trailingIcon = {
                            IconButton(onClick = {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _, y, m, d ->
                                        dueDateIso = String.format("%04d-%02d-%02d", y, m + 1, d)
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = "Pick date", tint = OrangePrimary)
                            }
                        },
                        colors = appOutlinedTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (task.type == TaskType.RECURRING) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = applyToSeries,
                                onCheckedChange = { applyToSeries = it },
                                colors = CheckboxDefaults.colors(checkedColor = OrangePrimary)
                            )
                            Text(
                                text = "Apply the Task & Doer change to all pending occurrences of this series",
                                fontSize = 12.sp,
                                color = TextDark
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAFAFA))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel", color = NavyPrimary)
                    }

                    Button(
                        onClick = {
                            onSave(title, doer, priority, dueDateIso, attachFile, applyToSeries)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteTaskDialog(
    task: TaskItem,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    var confirmationInput by remember { mutableStateOf("") }
    val isRecurring = task.type == TaskType.RECURRING

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavyPrimary)
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isRecurring) "Delete Recurring Series" else "Delete One-Time Task",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Warning Callout
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFF3CD))
                            .border(1.dp, Color(0xFFF0C040), RoundedCornerShape(8.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFF856404), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Warning — This action is permanent.",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF856404)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "You are about to delete:\n${task.task}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NavyPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isRecurring) {
                                    "All occurrences of this series — including completed ones — will be permanently removed from Recurring Tasks and the task definition."
                                } else {
                                    "The task will be permanently removed from One-Time Tasks. This cannot be undone."
                                },
                                fontSize = 11.sp,
                                color = Color(0xFFA00000)
                            )
                        }
                    }

                    Text(
                        text = "Type DELETE below to confirm:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )

                    OutlinedTextField(
                        value = confirmationInput,
                        onValueChange = { confirmationInput = it },
                        placeholder = { Text("Type DELETE") },
                        colors = appOutlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_confirm_delete")
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAFAFA))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel", color = NavyPrimary)
                    }

                    Button(
                        onClick = onConfirmDelete,
                        enabled = confirmationInput.trim() == "DELETE",
                        colors = ButtonDefaults.buttonColors(containerColor = StatusDelay),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_confirm_delete")
                    ) {
                        Text(
                            text = if (isRecurring) "Delete Series" else "Delete Task",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppsScriptConfigDialog(
    currentUrl: String,
    connectionStatus: String,
    isTesting: Boolean,
    onDismiss: () -> Unit,
    onUrlChange: (String) -> Unit,
    onTestConnection: () -> Unit
) {
    val context = LocalContext.current
    var urlInput by remember(currentUrl) { mutableStateOf(currentUrl) }
    var showCodeSnippet by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavyPrimary)
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Google Apps Script Connection",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Connect this Android app directly to your Google Apps Script Web App to sync tasks with your Google Sheets workbook in real time.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 17.sp
                    )

                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = {
                            urlInput = it
                            onUrlChange(it)
                        },
                        label = { Text("Web App URL") },
                        placeholder = { Text("https://script.google.com/macros/s/.../exec") },
                        colors = appOutlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_apps_script_url")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onTestConnection,
                            enabled = !isTesting && urlInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = NavySecondary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isTesting) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Test Connection", fontSize = 12.sp)
                            }
                        }

                        if (connectionStatus.isNotBlank()) {
                            Text(
                                text = connectionStatus,
                                fontSize = 11.sp,
                                color = if (connectionStatus.contains("success", ignoreCase = true)) StatusOk else StatusDelay,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Toggle snippet helper
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCodeSnippet = !showCodeSnippet }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (showCodeSnippet) "Hide doPost(e) Code.gs Helper ▲" else "View doPost(e) Code.gs Helper ▼",
                            color = OrangePrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (showCodeSnippet) {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Code.gs doPost snippet", AppsScriptClient.CODE_GS_DOPROST_SNIPPET))
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy code", tint = OrangePrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    if (showCodeSnippet) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(NavyPrimary)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = AppsScriptClient.CODE_GS_DOPROST_SNIPPET.trim(),
                                color = Color(0xFFC7D2FE),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAFAFA))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
