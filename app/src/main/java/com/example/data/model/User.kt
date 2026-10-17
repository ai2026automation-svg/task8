package com.example.data.model

import java.io.Serializable

enum class UserRole {
    ADMIN,
    MODERATOR,
    USER;

    companion object {
        fun fromString(value: String?): UserRole {
            val upper = value?.trim()?.uppercase() ?: ""
            return when {
                upper.startsWith("ADMIN") -> ADMIN
                upper.startsWith("MOD") -> MODERATOR
                else -> USER
            }
        }
    }
}

data class User(
    val row: Int = 0,
    val name: String,
    val designation: String = "",
    val email: String,
    val department: String = "",
    val role: UserRole = UserRole.USER
) : Serializable

data class UserPermissions(
    val view: Boolean = true,
    val assignRecurring: Boolean = false,
    val assignOneTime: Boolean = true,
    val assignOthers: Boolean = false,
    val manageUsers: Boolean = false,
    val exportR: Boolean = true,
    val deleteData: Boolean = false
) {
    companion object {
        fun forRole(role: UserRole): UserPermissions {
            return when (role) {
                UserRole.ADMIN -> UserPermissions(
                    view = true,
                    assignRecurring = true,
                    assignOneTime = true,
                    assignOthers = true,
                    manageUsers = true,
                    exportR = true,
                    deleteData = true
                )
                UserRole.MODERATOR -> UserPermissions(
                    view = true,
                    assignRecurring = true,
                    assignOneTime = true,
                    assignOthers = true,
                    manageUsers = false,
                    exportR = true,
                    deleteData = false
                )
                UserRole.USER -> UserPermissions(
                    view = true,
                    assignRecurring = false,
                    assignOneTime = true,
                    assignOthers = false,
                    manageUsers = false,
                    exportR = true,
                    deleteData = false
                )
            }
        }
    }
}

data class AuthSession(
    val token: String,
    val user: User,
    val perms: UserPermissions,
    val frequencies: List<String> = listOf("Daily", "Weekly", "Monthly", "Quarterly", "End of Month", "Half Yearly", "Yearly"),
    val priorities: List<String> = listOf("High", "Medium", "Low"),
    val weekdays: List<String> = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"),
    val doers: List<User> = emptyList()
)
