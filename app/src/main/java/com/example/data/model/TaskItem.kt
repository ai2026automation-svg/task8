package com.example.data.model

import java.io.Serializable

enum class TaskType {
    RECURRING,
    ONE_TIME
}

enum class TaskStatus {
    PENDING,
    ON_TIME,
    DELAY;

    val displayName: String
        get() = when (this) {
            PENDING -> "Pending"
            ON_TIME -> "On Time"
            DELAY -> "Delay"
        }

    companion object {
        fun fromString(value: String?): TaskStatus {
            val v = value?.trim()?.lowercase() ?: ""
            return when {
                v.contains("on time") -> ON_TIME
                v.contains("delay") -> DELAY
                else -> PENDING
            }
        }
    }
}

data class TaskItem(
    val id: String, // Sno for recurring, TaskId for One-Time
    val rowNum: Int = 0,
    val sno: Int = 0,
    val taskId: String = "",
    val task: String,
    val type: TaskType,
    val doerName: String,
    val doerEmail: String = "",
    val designation: String = "",
    val frequency: String = "",
    val priority: String = "Medium",
    val dueDate: String, // dd-MMM-yyyy
    val completedDate: String = "", // dd-MMM-yyyy HH:mm:ss
    val delay: String = "", // whole days
    val status: TaskStatus = TaskStatus.PENDING,
    val remarks: String = "",
    val attachFile: String = "", // Doer attachment link
    val assignerFile: String = "", // Assigner attachment link
    val assignedBy: String = "",
    val assignedByEmail: String = "",
    val canManage: Boolean = false,
    val dueEpochMillis: Long = 0L
) : Serializable

data class MisPeriodScore(
    val period: String,
    val total: Int,
    val onTime: Int,
    val notOnTime: Int,
    val pending: Int,
    val pctScore: Int,
    val sortKey: Long = 0L
)

data class DashboardMetrics(
    val total: Int = 0,
    val pending: Int = 0,
    val completed: Int = 0,
    val completedOnTime: Int = 0,
    val completedNotOnTime: Int = 0,
    val completionRate: Int = 0,
    val onTimeRate: Int = 0,
    val pctScore: Int = 0,
    val pctPending: Int = 0,
    val byStatus: Map<String, Int> = emptyMap(),
    val byDoer: Map<String, Int> = emptyMap(),
    val byFreq: Map<String, Int> = emptyMap(),
    val weekly: List<MisPeriodScore> = emptyList(),
    val monthly: List<MisPeriodScore> = emptyList(),
    val details: List<TaskItem> = emptyList(),
    val totalDetailsCount: Int = 0,
    val generatedAt: String = ""
)

data class ReminderSettings(
    val notifyOnAssign: Boolean = true,
    val notifyOnComplete: Boolean = true,
    val pendingReminders: Boolean = false,
    val reminderHour: Int = 9,
    val reminderEmails: String = ""
)
