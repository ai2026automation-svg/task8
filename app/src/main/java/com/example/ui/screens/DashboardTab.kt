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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FilterAlt
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DashboardMetrics
import com.example.data.model.TaskItem
import com.example.data.model.User
import com.example.ui.components.DoughnutStatusChart
import com.example.ui.components.KpiCard
import com.example.ui.components.SimpleBarChart
import com.example.ui.components.StatusBadge
import com.example.ui.components.appOutlinedTextFieldColors
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LineBorder
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.StatusDelay
import com.example.ui.theme.StatusOk
import com.example.ui.theme.StatusPending
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardTab(
    metrics: DashboardMetrics,
    misMode: String,
    scopedDoers: List<User>,
    selectedType: String,
    selectedFreq: String,
    selectedDoer: String,
    fromDate: String,
    toDate: String,
    detailsPage: Int,
    pageSize: Int,
    onFilterChange: (type: String, freq: String, doer: String, from: String, to: String) -> Unit,
    onClearFilters: () -> Unit,
    onMisModeChange: (String) -> Unit,
    onPageChange: (Int) -> Unit
) {
    var typeFilter by remember(selectedType) { mutableStateOf(selectedType) }
    var freqFilter by remember(selectedFreq) { mutableStateOf(selectedFreq) }
    var doerFilter by remember(selectedDoer) { mutableStateOf(selectedDoer) }
    var typeExpanded by remember { mutableStateOf(false) }
    var freqExpanded by remember { mutableStateOf(false) }
    var doerExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Head with title and filter controls
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MIS Dashboard",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Text(
                        text = if (metrics.generatedAt.isNotBlank()) "Updated ${metrics.generatedAt}" else "",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filters FlowRow
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Type dropdown
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = if (typeFilter.isEmpty()) "All types" else typeFilter,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            colors = appOutlinedTextFieldColors(),
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextDark, fontSize = 12.sp),
                            modifier = Modifier
                                .menuAnchor()
                                .width(130.dp)
                                .height(48.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            listOf("", "Recurring", "One Time").forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(if (item.isEmpty()) "All types" else item, fontSize = 12.sp) },
                                    onClick = {
                                        typeFilter = item
                                        typeExpanded = false
                                        onFilterChange(item, freqFilter, doerFilter, fromDate, toDate)
                                    }
                                )
                            }
                        }
                    }

                    // Frequency dropdown
                    ExposedDropdownMenuBox(
                        expanded = freqExpanded,
                        onExpandedChange = { freqExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = if (freqFilter.isEmpty()) "All freq" else freqFilter,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = freqExpanded) },
                            colors = appOutlinedTextFieldColors(),
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextDark, fontSize = 12.sp),
                            modifier = Modifier
                                .menuAnchor()
                                .width(130.dp)
                                .height(48.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = freqExpanded,
                            onDismissRequest = { freqExpanded = false }
                        ) {
                            val frequencies = listOf("", "Daily", "Weekly", "Monthly", "Quarterly", "End of Month", "Half Yearly", "Yearly")
                            frequencies.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(if (item.isEmpty()) "All frequency" else item, fontSize = 12.sp) },
                                    onClick = {
                                        freqFilter = item
                                        freqExpanded = false
                                        onFilterChange(typeFilter, item, doerFilter, fromDate, toDate)
                                    }
                                )
                            }
                        }
                    }

                    // Doer dropdown
                    ExposedDropdownMenuBox(
                        expanded = doerExpanded,
                        onExpandedChange = { doerExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = if (doerFilter.isEmpty()) "All doers" else doerFilter,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = doerExpanded) },
                            colors = appOutlinedTextFieldColors(),
                            textStyle = androidx.compose.ui.text.TextStyle(color = TextDark, fontSize = 12.sp),
                            modifier = Modifier
                                .menuAnchor()
                                .width(140.dp)
                                .height(48.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = doerExpanded,
                            onDismissRequest = { doerExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All doers", fontSize = 12.sp) },
                                onClick = {
                                    doerFilter = ""
                                    doerExpanded = false
                                    onFilterChange(typeFilter, freqFilter, "", fromDate, toDate)
                                }
                            )
                            scopedDoers.forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d.name, fontSize = 12.sp) },
                                    onClick = {
                                        doerFilter = d.name
                                        doerExpanded = false
                                        onFilterChange(typeFilter, freqFilter, d.name, fromDate, toDate)
                                    }
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            typeFilter = ""
                            freqFilter = ""
                            doerFilter = ""
                            onClearFilters()
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Clear", color = NavyPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // KPIs Grid (8 cards)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val cardModifier = Modifier
                .weight(1f)
                .widthIn(min = 140.dp)

            KpiCard(label = "Total Tasks", value = metrics.total.toString(), accentColor = NavyPrimary, modifier = cardModifier)
            KpiCard(label = "Pending", value = metrics.pending.toString(), accentColor = StatusDelay, modifier = cardModifier)
            KpiCard(label = "Pending %", value = "${metrics.pctPending}%", accentColor = StatusDelay, modifier = cardModifier)
            KpiCard(label = "Completed", value = metrics.completed.toString(), accentColor = StatusOk, modifier = cardModifier)
            KpiCard(label = "On Time", value = metrics.completedOnTime.toString(), accentColor = StatusOk, modifier = cardModifier)
            KpiCard(label = "Not On Time", value = metrics.completedNotOnTime.toString(), accentColor = OrangePrimary, modifier = cardModifier)
            KpiCard(label = "% Score", value = "${metrics.pctScore}%", accentColor = GoldAccent, modifier = cardModifier)
            KpiCard(label = "On-Time Rate", value = "${metrics.onTimeRate}%", accentColor = StatusOk, modifier = cardModifier)
        }

        // Charts Section
        DoughnutStatusChart(
            onTime = metrics.completedOnTime,
            delay = metrics.completedNotOnTime,
            pending = metrics.pending
        )

        SimpleBarChart(
            title = "Tasks by Doer",
            data = metrics.byDoer,
            barColor = NavySecondary
        )

        SimpleBarChart(
            title = "Tasks by Frequency",
            data = metrics.byFreq,
            barColor = OrangePrimary
        )

        // %Score by Period Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "%Score by Period",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )

                    // Segmented buttons
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, LineBorder, RoundedCornerShape(8.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .background(if (misMode == "week") NavyPrimary else Color.White)
                                .clickable { onMisModeChange("week") }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Weekly",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (misMode == "week") Color.White else TextMuted
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(if (misMode == "month") NavyPrimary else Color.White)
                                .clickable { onMisModeChange("month") }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Monthly",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (misMode == "month") Color.White else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val periods = if (misMode == "week") metrics.weekly else metrics.monthly
                if (periods.isEmpty()) {
                    Text(
                        text = "No period data available",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    // Scrollable Table
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Column {
                            // Table Header
                            Row(
                                modifier = Modifier
                                    .background(NavySecondary)
                                    .padding(vertical = 8.dp, horizontal = 10.dp)
                            ) {
                                TableCell(text = "Period", width = 160.dp, isHeader = true)
                                TableCell(text = "Total", width = 60.dp, isHeader = true)
                                TableCell(text = "On Time", width = 70.dp, isHeader = true)
                                TableCell(text = "Not On Time", width = 90.dp, isHeader = true)
                                TableCell(text = "Pending", width = 70.dp, isHeader = true)
                                TableCell(text = "%Score", width = 70.dp, isHeader = true)
                            }

                            // Table Rows
                            periods.forEach { p ->
                                val scoreColor = if (p.pctScore >= 0) StatusOk else StatusDelay
                                Row(
                                    modifier = Modifier
                                        .border(0.5.dp, LineBorder)
                                        .padding(vertical = 8.dp, horizontal = 10.dp)
                                ) {
                                    TableCell(text = p.period, width = 160.dp)
                                    TableCell(text = p.total.toString(), width = 60.dp)
                                    TableCell(text = p.onTime.toString(), width = 70.dp)
                                    TableCell(text = p.notOnTime.toString(), width = 90.dp)
                                    TableCell(text = p.pending.toString(), width = 70.dp)
                                    TableCell(text = "${p.pctScore}%", width = 70.dp, textColor = scoreColor, isBold = true)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Task Details Table with Pager
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Task Details",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Text(
                        text = "${metrics.totalDetailsCount} tasks",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val totalTasks = metrics.details
                val totalPages = ((totalTasks.size + pageSize - 1) / pageSize).coerceAtLeast(1)
                val currentPage = detailsPage.coerceIn(1, totalPages)
                val pageItems = totalTasks.drop((currentPage - 1) * pageSize).take(pageSize)

                if (pageItems.isEmpty()) {
                    Text(
                        text = "No tasks match the filter criteria",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
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
                                TableCell(text = "Task", width = 220.dp, isHeader = true)
                                TableCell(text = "Doer", width = 110.dp, isHeader = true)
                                TableCell(text = "Frequency", width = 90.dp, isHeader = true)
                                TableCell(text = "Type", width = 80.dp, isHeader = true)
                                TableCell(text = "Due Date", width = 100.dp, isHeader = true)
                                TableCell(text = "Completed", width = 110.dp, isHeader = true)
                                TableCell(text = "Status", width = 90.dp, isHeader = true)
                            }

                            pageItems.forEach { t ->
                                Row(
                                    modifier = Modifier
                                        .border(0.5.dp, LineBorder)
                                        .padding(vertical = 8.dp, horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TableCell(text = t.task, width = 220.dp)
                                    TableCell(text = t.doerName, width = 110.dp)
                                    TableCell(text = if (t.frequency.isNotBlank()) t.frequency else "—", width = 90.dp)
                                    TableCell(text = if (t.type.name == "RECURRING") "Recurring" else "One Time", width = 80.dp)
                                    TableCell(text = t.dueDate, width = 100.dp)
                                    TableCell(text = if (t.completedDate.isNotBlank()) t.completedDate.take(11) else "—", width = 110.dp)
                                    Box(modifier = Modifier.width(90.dp)) {
                                        StatusBadge(status = t.status)
                                    }
                                }
                            }
                        }
                    }

                    // Pager Controls
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val fromIndex = (currentPage - 1) * pageSize + 1
                        val toIndex = (currentPage * pageSize).coerceAtMost(totalTasks.size)
                        Text(
                            text = "Showing $fromIndex–$toIndex of ${totalTasks.size}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onPageChange(currentPage - 1) },
                                enabled = currentPage > 1,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous")
                            }

                            Text(
                                text = "Page $currentPage / $totalPages",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )

                            IconButton(
                                onClick = { onPageChange(currentPage + 1) },
                                enabled = currentPage < totalPages,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Next")
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
    textColor: Color = if (isHeader) Color.White else TextDark,
    isBold: Boolean = false
) {
    Text(
        text = text,
        fontSize = if (isHeader) 11.sp else 11.5.sp,
        fontWeight = if (isHeader || isBold) FontWeight.Bold else FontWeight.Normal,
        color = textColor,
        modifier = Modifier.width(width)
    )
}
