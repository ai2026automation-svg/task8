package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.data.model.TaskItem
import com.example.data.model.User
import com.example.ui.components.StatusBadge
import com.example.ui.components.appOutlinedTextFieldColors
import com.example.ui.theme.LineBorder
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusDelay
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecurringTasksTab(
    tasks: List<TaskItem>,
    currentUser: User?,
    scopedDoers: List<User>,
    search: String,
    doerFilter: String,
    freqFilter: String,
    statusFilter: String,
    groupKey: String,
    horizonDays: Int,
    onFilterChange: (search: String, doer: String, freq: String, status: String, group: String, horizon: Int) -> Unit,
    onOpenComplete: (TaskItem) -> Unit,
    onOpenEdit: (TaskItem) -> Unit,
    onOpenDeleteSeries: (TaskItem) -> Unit
) {
    val context = LocalContext.current
    var searchInput by remember(search) { mutableStateOf(search) }
    var doerInput by remember(doerFilter) { mutableStateOf(doerFilter) }
    var freqInput by remember(freqFilter) { mutableStateOf(freqFilter) }
    var statusInput by remember(statusFilter) { mutableStateOf(statusFilter) }
    var groupInput by remember(groupKey) { mutableStateOf(groupKey) }
    var horizonInput by remember(horizonDays) { mutableStateOf(horizonDays) }

    var doerExpanded by remember { mutableStateOf(false) }
    var freqExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var groupExpanded by remember { mutableStateOf(false) }
    var horizonExpanded by remember { mutableStateOf(false) }

    // Map to keep track of collapsed state of each group
    val collapsedGroups = remember { mutableStateMapOf<String, Boolean>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Toolbar Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Recurring Tasks",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Search
                    OutlinedTextField(
                        value = searchInput,
                        onValueChange = {
                            searchInput = it
                            onFilterChange(it, doerInput, freqInput, statusInput, groupInput, horizonInput)
                        },
                        placeholder = { Text("Search task, doer…", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                        colors = appOutlinedTextFieldColors(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = TextDark, fontSize = 12.sp),
                        modifier = Modifier
                            .width(180.dp)
                            .height(48.dp)
                    )

                    // Doer Dropdown
                    ExposedDropdownMenuBox(
                        expanded = doerExpanded,
                        onExpandedChange = { doerExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = if (doerInput.isEmpty()) "All doers" else doerInput,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = doerExpanded) },
                            colors = appOutlinedTextFieldColors(),
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextDark, fontSize = 12.sp),
                            modifier = Modifier
                                .menuAnchor()
                                .width(130.dp)
                                .height(48.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = doerExpanded,
                            onDismissRequest = { doerExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All doers", fontSize = 12.sp) },
                                onClick = {
                                    doerInput = ""
                                    doerExpanded = false
                                    onFilterChange(searchInput, "", freqInput, statusInput, groupInput, horizonInput)
                                }
                            )
                            scopedDoers.forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d.name, fontSize = 12.sp) },
                                    onClick = {
                                        doerInput = d.name
                                        doerExpanded = false
                                        onFilterChange(searchInput, d.name, freqInput, statusInput, groupInput, horizonInput)
                                    }
                                )
                            }
                        }
                    }

                    // Frequency Dropdown
                    ExposedDropdownMenuBox(
                        expanded = freqExpanded,
                        onExpandedChange = { freqExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = if (freqInput.isEmpty()) "All freq" else freqInput,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = freqExpanded) },
                            colors = appOutlinedTextFieldColors(),
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextDark, fontSize = 12.sp),
                            modifier = Modifier
                                .menuAnchor()
                                .width(120.dp)
                                .height(48.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = freqExpanded,
                            onDismissRequest = { freqExpanded = false }
                        ) {
                            listOf("", "Daily", "Weekly", "Monthly", "Quarterly", "End of Month", "Half Yearly", "Yearly").forEach { f ->
                                DropdownMenuItem(
                                    text = { Text(if (f.isEmpty()) "All freq" else f, fontSize = 12.sp) },
                                    onClick = {
                                        freqInput = f
                                        freqExpanded = false
                                        onFilterChange(searchInput, doerInput, f, statusInput, groupInput, horizonInput)
                                    }
                                )
                            }
                        }
                    }

                    // Status Dropdown
                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = if (statusInput.isEmpty()) "All status" else statusInput,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                            colors = appOutlinedTextFieldColors(),
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextDark, fontSize = 12.sp),
                            modifier = Modifier
                                .menuAnchor()
                                .width(120.dp)
                                .height(48.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false }
                        ) {
                            listOf("", "Pending", "On Time", "Delay").forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(if (st.isEmpty()) "All status" else st, fontSize = 12.sp) },
                                    onClick = {
                                        statusInput = st
                                        statusExpanded = false
                                        onFilterChange(searchInput, doerInput, freqInput, st, groupInput, horizonInput)
                                    }
                                )
                            }
                        }
                    }

                    // Group by Dropdown
                    ExposedDropdownMenuBox(
                        expanded = groupExpanded,
                        onExpandedChange = { groupExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = "Group: ${groupInput.replaceFirstChar { it.uppercase() }}",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                            colors = appOutlinedTextFieldColors(),
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextDark, fontSize = 12.sp),
                            modifier = Modifier
                                .menuAnchor()
                                .width(135.dp)
                                .height(48.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = groupExpanded,
                            onDismissRequest = { groupExpanded = false }
                        ) {
                            listOf("doer" to "Doer", "frequency" to "Frequency", "status" to "Status", "none" to "No grouping").forEach { g ->
                                DropdownMenuItem(
                                    text = { Text(g.second, fontSize = 12.sp) },
                                    onClick = {
                                        groupInput = g.first
                                        groupExpanded = false
                                        onFilterChange(searchInput, doerInput, freqInput, statusInput, g.first, horizonInput)
                                    }
                                )
                            }
                        }
                    }

                    // Horizon Dropdown
                    ExposedDropdownMenuBox(
                        expanded = horizonExpanded,
                        onExpandedChange = { horizonExpanded = it }
                    ) {
                        val horizonLabel = if (horizonInput == 0) "Show: All" else "Next: $horizonInput d"
                        OutlinedTextField(
                            value = horizonLabel,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = horizonExpanded) },
                            colors = appOutlinedTextFieldColors(),
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextDark, fontSize = 12.sp),
                            modifier = Modifier
                                .menuAnchor()
                                .width(120.dp)
                                .height(48.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = horizonExpanded,
                            onDismissRequest = { horizonExpanded = false }
                        ) {
                            listOf(0 to "All", 7 to "7 days", 15 to "15 days", 30 to "30 days", 90 to "90 days").forEach { h ->
                                DropdownMenuItem(
                                    text = { Text(h.second, fontSize = 12.sp) },
                                    onClick = {
                                        horizonInput = h.first
                                        horizonExpanded = false
                                        onFilterChange(searchInput, doerInput, freqInput, statusInput, groupInput, h.first)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Grouped Tasks List
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No recurring tasks match.",
                    fontSize = 14.sp,
                    color = TextMuted
                )
            }
        } else {
            val groups = when (groupInput) {
                "none" -> mapOf("All Tasks" to tasks)
                "frequency" -> tasks.groupBy { if (it.frequency.isNotBlank()) it.frequency else "—" }
                "status" -> tasks.groupBy { it.status.displayName }
                else -> tasks.groupBy { if (it.doerName.isNotBlank()) it.doerName else "Unassigned" }
            }

            groups.forEach { (groupTitle, groupTasks) ->
                val isCollapsed = collapsedGroups[groupTitle] ?: false

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Group Head
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF7F9FC))
                                .clickable {
                                    collapsedGroups[groupTitle] = !isCollapsed
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = groupTitle,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(NavyPrimary)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = groupTasks.size.toString(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Icon(
                                imageVector = if (isCollapsed) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                contentDescription = if (isCollapsed) "Expand" else "Collapse",
                                tint = TextMuted
                            )
                        }

                        // Group Items
                        AnimatedVisibility(visible = !isCollapsed) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                ) {
                                    Column {
                                        // Header
                                        Row(
                                            modifier = Modifier
                                                .background(NavySecondary)
                                                .padding(vertical = 8.dp, horizontal = 12.dp)
                                        ) {
                                            TableCell("S.No.", 50.dp, isHeader = true)
                                            TableCell("Task", 220.dp, isHeader = true)
                                            TableCell("Doer", 110.dp, isHeader = true)
                                            TableCell("Designation", 110.dp, isHeader = true)
                                            TableCell("Freq", 80.dp, isHeader = true)
                                            TableCell("Due Date", 95.dp, isHeader = true)
                                            TableCell("Completed", 100.dp, isHeader = true)
                                            TableCell("Delay", 60.dp, isHeader = true)
                                            TableCell("Status", 85.dp, isHeader = true)
                                            TableCell("Remarks", 140.dp, isHeader = true)
                                            TableCell("File", 65.dp, isHeader = true)
                                            TableCell("Actions", 160.dp, isHeader = true)
                                        }

                                        // Task rows
                                        groupTasks.forEach { task ->
                                            val isDone = task.completedDate.isNotBlank()
                                            val meName = currentUser?.name?.trim()?.lowercase() ?: ""
                                            val meEmail = currentUser?.email?.trim()?.lowercase() ?: ""
                                            val isMine = task.doerName.trim().lowercase() == meName ||
                                                (task.doerEmail.isNotBlank() && task.doerEmail.trim().lowercase() == meEmail)

                                            Row(
                                                modifier = Modifier
                                                    .border(0.5.dp, LineBorder)
                                                    .padding(vertical = 8.dp, horizontal = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                TableCell(task.sno.toString(), 50.dp)
                                                TableCell(task.task, 220.dp, isBold = true)
                                                TableCell(task.doerName, 110.dp)
                                                TableCell(task.designation, 110.dp)
                                                TableCell(task.frequency, 80.dp)
                                                TableCell(task.dueDate, 95.dp)
                                                TableCell(if (task.completedDate.isNotBlank()) task.completedDate.take(11) else "—", 100.dp)
                                                TableCell(if (task.delay.isNotBlank()) "${task.delay}d" else "—", 60.dp)

                                                Box(modifier = Modifier.width(85.dp)) {
                                                    StatusBadge(status = task.status)
                                                }

                                                TableCell(if (task.remarks.isNotBlank()) task.remarks else "—", 140.dp)

                                                // File link
                                                Box(modifier = Modifier.width(65.dp)) {
                                                    if (task.attachFile.isNotBlank()) {
                                                        IconButton(
                                                            onClick = {
                                                                try {
                                                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(task.attachFile)))
                                                                } catch (_: Exception) {}
                                                            },
                                                            modifier = Modifier.size(28.dp)
                                                        ) {
                                                            Icon(Icons.Default.OpenInNew, contentDescription = "Open file", tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                                        }
                                                    } else {
                                                        Text("—", fontSize = 11.sp, color = TextMuted)
                                                    }
                                                }

                                                // Actions
                                                Row(
                                                    modifier = Modifier.width(160.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    if (!isDone && isMine) {
                                                        Button(
                                                            onClick = { onOpenComplete(task) },
                                                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                                            shape = RoundedCornerShape(6.dp),
                                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                            modifier = Modifier.height(28.dp)
                                                        ) {
                                                            Text("Update", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    } else if (isDone) {
                                                        OutlinedButton(
                                                            onClick = {},
                                                            enabled = false,
                                                            shape = RoundedCornerShape(6.dp),
                                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                            modifier = Modifier.height(28.dp)
                                                        ) {
                                                            Text("Done", fontSize = 11.sp)
                                                        }
                                                    }

                                                    if (task.canManage && !isDone) {
                                                        OutlinedButton(
                                                            onClick = { onOpenEdit(task) },
                                                            shape = RoundedCornerShape(6.dp),
                                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                            modifier = Modifier.height(28.dp)
                                                        ) {
                                                            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(12.dp))
                                                        }
                                                    }

                                                    if (task.canManage) {
                                                        OutlinedButton(
                                                            onClick = { onOpenDeleteSeries(task) },
                                                            shape = RoundedCornerShape(6.dp),
                                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusDelay),
                                                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusDelay.copy(alpha = 0.5f)),
                                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                            modifier = Modifier.height(28.dp)
                                                        ) {
                                                            Icon(Icons.Default.Delete, contentDescription = "Delete series", modifier = Modifier.size(12.dp))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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
