package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.data.model.UserPermissions
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
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AssignTaskTab(
    mode: String,
    permissions: UserPermissions,
    scopedDoers: List<User>,
    rTask: String,
    rDoer: String,
    rDesignation: String,
    rFrequency: String,
    rStartDate: String,
    rOffDays: Set<Int>,
    rMsg: String,
    rIsError: Boolean,
    isAssigningRecurring: Boolean,
    oDetails: String,
    oDoer: String,
    oPriority: String,
    oDueDate: String,
    oAttachFile: String,
    oMsg: String,
    oIsError: Boolean,
    isAssigningOneTime: Boolean,
    onModeChange: (String) -> Unit,
    onUpdateRecurring: (task: String?, doer: String?, freq: String?, start: String?) -> Unit,
    onToggleOffDay: (Int) -> Unit,
    onSubmitRecurring: () -> Unit,
    onUpdateOneTime: (details: String?, doer: String?, priority: String?, due: String?, file: String?) -> Unit,
    onSubmitOneTime: () -> Unit
) {
    val context = LocalContext.current
    var rDoerExpanded by remember { mutableStateOf(false) }
    var rFreqExpanded by remember { mutableStateOf(false) }
    var oDoerExpanded by remember { mutableStateOf(false) }
    var oPriorityExpanded by remember { mutableStateOf(false) }

    val weekdays = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val frequencies = listOf("Daily", "Weekly", "Monthly", "Quarterly", "End of Month", "Half Yearly", "Yearly")
    val priorities = listOf("High", "Medium", "Low")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Assign Task",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = NavyPrimary
        )

        // Mode switch (Recurring vs One-Time)
        if (permissions.assignRecurring && permissions.assignOneTime) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, LineBorder, RoundedCornerShape(8.dp))
            ) {
                Box(
                    modifier = Modifier
                        .background(if (mode == "rec") NavyPrimary else Color.White)
                        .clickable { onModeChange("rec") }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Recurring",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (mode == "rec") Color.White else TextMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .background(if (mode == "ott") NavyPrimary else Color.White)
                        .clickable { onModeChange("ott") }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "One-Time",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (mode == "ott") Color.White else TextMuted
                    )
                }
            }
        }

        if (mode == "rec" && permissions.assignRecurring) {
            // Recurring Task Form
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Task Title
                    OutlinedTextField(
                        value = rTask,
                        onValueChange = { onUpdateRecurring(it, null, null, null) },
                        label = { Text("Task *") },
                        placeholder = { Text("e.g. GST Monthly Filing, Bank Reconciliation") },
                        colors = appOutlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_r_task")
                    )

                    // Doer & Designation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = rDoerExpanded,
                            onExpandedChange = { rDoerExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = if (rDoer.isEmpty()) "Select Doer *" else rDoer,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Doer *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rDoerExpanded) },
                                colors = appOutlinedTextFieldColors(),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = rDoerExpanded,
                                onDismissRequest = { rDoerExpanded = false }
                            ) {
                                scopedDoers.forEach { d ->
                                    DropdownMenuItem(
                                        text = { Text("${d.name} (${d.designation})") },
                                        onClick = {
                                            rDoerExpanded = false
                                            onUpdateRecurring(null, d.name, null, null)
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = rDesignation,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Designation") },
                            colors = appOutlinedTextFieldColors(containerColor = Color(0xFFF7F9FC)),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Frequency & Start Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = rFreqExpanded,
                            onExpandedChange = { rFreqExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = rFrequency,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Frequency *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rFreqExpanded) },
                                colors = appOutlinedTextFieldColors(),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = rFreqExpanded,
                                onDismissRequest = { rFreqExpanded = false }
                            ) {
                                frequencies.forEach { f ->
                                    DropdownMenuItem(
                                        text = { Text(f) },
                                        onClick = {
                                            rFreqExpanded = false
                                            onUpdateRecurring(null, null, f, null)
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = rStartDate,
                            onValueChange = { onUpdateRecurring(null, null, null, it) },
                            label = { Text("Start Date *") },
                            placeholder = { Text("yyyy-MM-dd") },
                            trailingIcon = {
                                IconButton(onClick = {
                                    val cal = Calendar.getInstance()
                                    DatePickerDialog(
                                        context,
                                        { _, y, m, d ->
                                            val iso = String.format("%04d-%02d-%02d", y, m + 1, d)
                                            onUpdateRecurring(null, null, null, iso)
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
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Weekly Off Chips
                    Column {
                        Text(
                            text = "Weekly Off (skipped for Daily / shifted earlier for others)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF374151)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            weekdays.forEachIndexed { index, dayName ->
                                val isSelected = rOffDays.contains(index)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) NavyPrimary else Color.White)
                                        .border(1.dp, if (isSelected) NavyPrimary else LineBorder, RoundedCornerShape(20.dp))
                                        .clickable { onToggleOffDay(index) }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = dayName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isSelected) Color.White else TextDark
                                    )
                                }
                            }
                        }
                    }

                    // Actions & Message
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Button(
                            onClick = onSubmitRecurring,
                            enabled = !isAssigningRecurring,
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("btn_rec_submit")
                        ) {
                            if (isAssigningRecurring) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Generate Tasks", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (rMsg.isNotBlank()) {
                            Text(
                                text = rMsg,
                                fontSize = 12.sp,
                                color = if (rIsError) StatusDelay else StatusOk,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Horizon hint
                    Text(
                        text = "Daily generates 365 days (off-days skipped). Weekly 52, Monthly 12, Quarterly 8, End of Month 12, Half-Yearly across 2 years, Yearly 2. For non-daily, an off-day due date shifts to the previous working day.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                }
            }
        } else {
            // One-Time Task Form
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Task Details
                    OutlinedTextField(
                        value = oDetails,
                        onValueChange = { onUpdateOneTime(it, null, null, null, null) },
                        label = { Text("Task Details *") },
                        placeholder = { Text("Describe the task, deliverables, and instructions…") },
                        minLines = 3,
                        colors = appOutlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_o_details")
                    )

                    // Assign To & Priority
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = oDoerExpanded,
                            onExpandedChange = { if (permissions.assignOthers) oDoerExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = if (oDoer.isEmpty()) "Select Assignee *" else oDoer,
                                onValueChange = {},
                                readOnly = true,
                                enabled = permissions.assignOthers,
                                label = { Text("Assign To *") },
                                trailingIcon = {
                                    if (permissions.assignOthers) {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = oDoerExpanded)
                                    }
                                },
                                colors = appOutlinedTextFieldColors(),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            if (permissions.assignOthers) {
                                ExposedDropdownMenu(
                                    expanded = oDoerExpanded,
                                    onDismissRequest = { oDoerExpanded = false }
                                ) {
                                    scopedDoers.forEach { d ->
                                        DropdownMenuItem(
                                            text = { Text(d.name) },
                                            onClick = {
                                                oDoerExpanded = false
                                                onUpdateOneTime(null, d.name, null, null, null)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = oPriorityExpanded,
                            onExpandedChange = { oPriorityExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = oPriority,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Priority") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = oPriorityExpanded) },
                                colors = appOutlinedTextFieldColors(),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = oPriorityExpanded,
                                onDismissRequest = { oPriorityExpanded = false }
                            ) {
                                priorities.forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text(p) },
                                        onClick = {
                                            oPriorityExpanded = false
                                            onUpdateOneTime(null, null, p, null, null)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Due Date
                    OutlinedTextField(
                        value = oDueDate,
                        onValueChange = { onUpdateOneTime(null, null, null, it, null) },
                        label = { Text("Due Date *") },
                        placeholder = { Text("yyyy-MM-dd") },
                        trailingIcon = {
                            IconButton(onClick = {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _, y, m, d ->
                                        val iso = String.format("%04d-%02d-%02d", y, m + 1, d)
                                        onUpdateOneTime(null, null, null, iso, null)
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

                    // Reference File / Link
                    OutlinedTextField(
                        value = oAttachFile,
                        onValueChange = { onUpdateOneTime(null, null, null, null, it) },
                        label = { Text("Attach File / Link (optional)") },
                        placeholder = { Text("https://drive.google.com/... or paste reference document link") },
                        colors = appOutlinedTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Button(
                            onClick = onSubmitOneTime,
                            enabled = !isAssigningOneTime,
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("btn_ott_submit")
                        ) {
                            if (isAssigningOneTime) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Assign Task", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (oMsg.isNotBlank()) {
                            Text(
                                text = oMsg,
                                fontSize = 12.sp,
                                color = if (oIsError) StatusDelay else StatusOk,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Text(
                        text = "Admins can assign to anyone, moderators to their own team, and users to themselves only.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}
