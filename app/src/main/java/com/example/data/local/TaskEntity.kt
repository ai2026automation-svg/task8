package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.TaskItem
import com.example.data.model.TaskStatus
import com.example.data.model.TaskType

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val rowNum: Int = 0,
    val sno: Int = 0,
    val taskId: String = "",
    val task: String,
    val type: String,
    val doerName: String,
    val doerEmail: String = "",
    val designation: String = "",
    val frequency: String = "",
    val priority: String = "Medium",
    val dueDate: String,
    val completedDate: String = "",
    val delay: String = "",
    val status: String = "Pending",
    val remarks: String = "",
    val attachFile: String = "",
    val assignerFile: String = "",
    val assignedBy: String = "",
    val assignedByEmail: String = "",
    val dueEpochMillis: Long = 0L
) {
    fun toTaskItem(canManage: Boolean = false): TaskItem {
        return TaskItem(
            id = id,
            rowNum = rowNum,
            sno = sno,
            taskId = taskId,
            task = task,
            type = if (type == "RECURRING") TaskType.RECURRING else TaskType.ONE_TIME,
            doerName = doerName,
            doerEmail = doerEmail,
            designation = designation,
            frequency = frequency,
            priority = priority,
            dueDate = dueDate,
            completedDate = completedDate,
            delay = delay,
            status = TaskStatus.fromString(status),
            remarks = remarks,
            attachFile = attachFile,
            assignerFile = assignerFile,
            assignedBy = assignedBy,
            assignedByEmail = assignedByEmail,
            canManage = canManage,
            dueEpochMillis = dueEpochMillis
        )
    }

    companion object {
        fun fromTaskItem(item: TaskItem): TaskEntity {
            return TaskEntity(
                id = item.id,
                rowNum = item.rowNum,
                sno = item.sno,
                taskId = item.taskId,
                task = item.task,
                type = item.type.name,
                doerName = item.doerName,
                doerEmail = item.doerEmail,
                designation = item.designation,
                frequency = item.frequency,
                priority = item.priority,
                dueDate = item.dueDate,
                completedDate = item.completedDate,
                delay = item.delay,
                status = item.status.displayName,
                remarks = item.remarks,
                attachFile = item.attachFile,
                assignerFile = item.assignerFile,
                assignedBy = item.assignedBy,
                assignedByEmail = item.assignedByEmail,
                dueEpochMillis = item.dueEpochMillis
            )
        }
    }
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String,
    val rowNum: Int = 0,
    val name: String,
    val designation: String = "",
    val department: String = "",
    val role: String = "User"
)
