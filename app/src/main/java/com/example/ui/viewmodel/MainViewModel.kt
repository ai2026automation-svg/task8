package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.TaskPreferences
import com.example.data.model.DashboardMetrics
import com.example.data.model.ReminderSettings
import com.example.data.model.TaskItem
import com.example.data.model.TaskType
import com.example.data.model.User
import com.example.data.model.UserPermissions
import com.example.data.model.UserRole
import com.example.data.remote.AppsScriptClient
import com.example.data.repository.TaskRepository
import com.example.logic.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

enum class AppTab(val title: String) {
    DASHBOARD("Dashboard"),
    RECURRING("Recurring Tasks"),
    ONETIME("One-Time Tasks"),
    ASSIGN("Assign Task"),
    USERS("Users")
}

data class MainUiState(
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val permissions: UserPermissions = UserPermissions(),
    val scopedDoers: List<User> = emptyList(),
    val currentTab: AppTab = AppTab.DASHBOARD,

    // Login state
    val loginStep: Int = 1, // 1=email, 2=code
    val loginEmail: String = "",
    val loginCode: String = "",
    val loginMsg: String = "",
    val loginIsError: Boolean = false,
    val isLoggingIn: Boolean = false,
    val lastGeneratedOtpHint: String = "",

    // Connection
    val appsScriptUrl: String = "",
    val connectionStatus: String = "",
    val isTestingConnection: Boolean = false,
    val showServerConfigDialog: Boolean = false,
    val showProfileDialog: Boolean = false,

    // Dashboard
    val dashboardMetrics: DashboardMetrics = DashboardMetrics(),
    val misMode: String = "week", // "week" or "month"
    val dashTypeFilter: String = "",
    val dashFreqFilter: String = "",
    val dashDoerFilter: String = "",
    val dashFromFilter: String = "",
    val dashToFilter: String = "",
    val detailsPage: Int = 1,
    val detailsPageSize: Int = 10,

    // Recurring tab
    val recurringTasks: List<TaskItem> = emptyList(),
    val recSearch: String = "",
    val recDoerFilter: String = "",
    val recFreqFilter: String = "",
    val recStatusFilter: String = "",
    val recGroup: String = "doer", // "doer", "frequency", "status", "none"
    val recHorizon: Int = 0,
    val recPage: Int = 1,
    val recPageSize: Int = 20,

    // One-time tab
    val oneTimeTasks: List<TaskItem> = emptyList(),
    val ottSearch: String = "",
    val ottDoerFilter: String = "",
    val ottPriorityFilter: String = "",
    val ottStatusFilter: String = "",
    val ottGroup: String = "status", // "status", "doer", "priority", "none"
    val ottHorizon: Int = 0,
    val ottPage: Int = 1,
    val ottPageSize: Int = 20,

    // Assign tab
    val assignMode: String = "rec", // "rec" or "ott"
    val rTask: String = "",
    val rDoer: String = "",
    val rDesignation: String = "",
    val rFrequency: String = "Monthly",
    val rStartDate: String = "",
    val rOffDays: Set<Int> = setOf(0), // Default Sunday = 0
    val rMsg: String = "",
    val rIsError: Boolean = false,
    val isAssigningRecurring: Boolean = false,

    val oDetails: String = "",
    val oDoer: String = "",
    val oPriority: String = "Medium",
    val oDueDate: String = "",
    val oAttachFile: String = "",
    val oMsg: String = "",
    val oIsError: Boolean = false,
    val isAssigningOneTime: Boolean = false,

    // Users tab
    val usersList: List<User> = emptyList(),
    val uName: String = "",
    val uDesignation: String = "",
    val uEmail: String = "",
    val uDepartment: String = "",
    val uRole: UserRole = UserRole.USER,
    val uMsg: String = "",
    val uIsError: Boolean = false,
    val settings: ReminderSettings = ReminderSettings(),
    val settingsMsg: String = "",

    // Dialogs
    val activeCompleteTask: TaskItem? = null,
    val activeEditTask: TaskItem? = null,
    val activeDeleteTask: TaskItem? = null,

    val toastMessage: String? = null
)

class MainViewModel(
    private val repository: TaskRepository,
    private val preferences: TaskPreferences,
    private val apiClient: AppsScriptClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
            val savedToken = preferences.sessionToken
            val savedEmail = preferences.userEmail
            val savedUrl = preferences.appsScriptUrl

            _uiState.update {
                it.copy(
                    appsScriptUrl = savedUrl,
                    settings = repository.getSettings(),
                    rStartDate = DateUtils.formatIso(Date()),
                    oDueDate = DateUtils.formatIso(DateUtils.addDays(Date(), 3))
                )
            }

            if (savedToken.isNotBlank() && savedEmail.isNotBlank()) {
                // Keep the user signed in after the app is closed: restore the saved session
                // from local storage (no server check with a fake code). The user is only
                // signed out when they tap Logout.
                val savedRole = UserRole.fromString(preferences.userRole)
                val savedUser = User(
                    name = preferences.userName.ifBlank { savedEmail.substringBefore("@") },
                    designation = preferences.userDesignation,
                    email = savedEmail,
                    department = preferences.userDepartment,
                    role = savedRole
                )
                val savedDoers = repository.getScopedDoers(savedUser)
                onLoginSuccess(savedUser, UserPermissions.forRole(savedRole), savedDoers)
            }
        }
    }

    fun setAppTab(tab: AppTab) {
        _uiState.update { it.copy(currentTab = tab) }
        refreshData()
    }

    fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    // AUTH
    fun setLoginStep(step: Int) {
        _uiState.update { it.copy(loginStep = step, loginMsg = "") }
    }

    fun submitAppsScriptUrlAndProceed(url: String) {
        val trimmed = url.trim()
        preferences.appsScriptUrl = trimmed
        _uiState.update {
            it.copy(
                appsScriptUrl = trimmed,
                loginStep = 1,
                loginMsg = if (trimmed.isNotBlank()) "Connected to Apps Script! Enter your email to authenticate." else "",
                loginIsError = false
            )
        }
    }

    fun backToUrl() {
        _uiState.update { it.copy(loginStep = 0, loginMsg = "") }
    }

    fun setLoginEmail(email: String) {
        _uiState.update { it.copy(loginEmail = email, loginMsg = "") }
    }

    fun setLoginCode(code: String) {
        _uiState.update { it.copy(loginCode = code, loginMsg = "") }
    }

    fun requestCode() {
        val email = _uiState.value.loginEmail.trim()
        if (email.isBlank() || !email.contains("@")) {
            _uiState.update { it.copy(loginMsg = "Please enter a valid email address.", loginIsError = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingIn = true, loginMsg = "") }
            val result = repository.requestLoginCode(email)
            _uiState.update { it.copy(isLoggingIn = false) }

            result.fold(
                onSuccess = { _ ->
                    _uiState.update {
                        it.copy(
                            loginStep = 2,
                            loginMsg = "",
                            loginIsError = false
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            loginMsg = AppsScriptClient.cleanErrorMessage(err.message),
                            loginIsError = true
                        )
                    }
                }
            )
        }
    }

    fun verifyCode() {
        val email = _uiState.value.loginEmail.trim()
        val code = _uiState.value.loginCode.trim()

        if (code.length != 6) {
            _uiState.update { it.copy(loginMsg = "Please enter the 6-digit code.", loginIsError = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingIn = true, loginMsg = "") }
            val result = repository.verifyLoginCode(email, code)
            _uiState.update { it.copy(isLoggingIn = false) }

            result.fold(
                onSuccess = { session ->
                    onLoginSuccess(session.user, session.perms, session.doers)
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            loginMsg = AppsScriptClient.cleanErrorMessage(err.message),
                            loginIsError = true
                        )
                    }
                }
            )
        }
    }

    private fun onLoginSuccess(user: User, perms: UserPermissions, doers: List<User>) {
        _uiState.update {
            it.copy(
                isLoggedIn = true,
                currentUser = user,
                permissions = perms,
                scopedDoers = doers,
                loginStep = 1,
                loginEmail = "",
                loginCode = "",
                loginMsg = "",
                rDoer = doers.firstOrNull()?.name ?: "",
                rDesignation = doers.firstOrNull()?.designation ?: "",
                oDoer = if (perms.assignOthers) (doers.firstOrNull()?.name ?: "") else user.name
            )
        }
        showToast("Signed in as ${user.name} (${user.role})")
        refreshData()
    }

    fun backToEmail() {
        _uiState.update { it.copy(loginStep = 1, loginCode = "", loginMsg = "") }
    }

    fun logout() {
        preferences.clearSession()
        _uiState.update {
            it.copy(
                isLoggedIn = false,
                currentUser = null,
                loginStep = 1,
                loginEmail = "",
                loginCode = ""
            )
        }
        showToast("You have been signed out.")
    }

    // SERVER / APPS SCRIPT CONFIG
    fun toggleProfileDialog(show: Boolean) {
        _uiState.update { it.copy(showProfileDialog = show) }
    }

    fun toggleServerConfigDialog(show: Boolean) {
        _uiState.update { it.copy(showServerConfigDialog = show, connectionStatus = "") }
    }

    fun updateAppsScriptUrl(url: String) {
        preferences.appsScriptUrl = url
        _uiState.update { it.copy(appsScriptUrl = url) }
    }

    fun testAppsScriptConnection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTestingConnection = true, connectionStatus = "Testing connection…") }
            val res = apiClient.testConnection()
            _uiState.update {
                it.copy(
                    isTestingConnection = false,
                    connectionStatus = res.getOrElse { e -> "Failed: ${e.message}" }
                )
            }
        }
    }

    // REFRESH ALL
    fun refreshData() {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            repository.syncFromAppsScript(user)
            val scoped = repository.getScopedDoers(user)
            val metrics = repository.calculateDashboard(
                currentUser = user,
                typeFilter = _uiState.value.dashTypeFilter,
                freqFilter = _uiState.value.dashFreqFilter,
                doerFilter = _uiState.value.dashDoerFilter,
                fromDate = _uiState.value.dashFromFilter,
                toDate = _uiState.value.dashToFilter
            )
            val recTasks = repository.getTasks(
                currentUser = user,
                type = TaskType.RECURRING,
                search = _uiState.value.recSearch,
                doer = _uiState.value.recDoerFilter,
                frequency = _uiState.value.recFreqFilter,
                status = _uiState.value.recStatusFilter,
                horizonDays = _uiState.value.recHorizon
            )
            val ottTasks = repository.getTasks(
                currentUser = user,
                type = TaskType.ONE_TIME,
                search = _uiState.value.ottSearch,
                doer = _uiState.value.ottDoerFilter,
                priority = _uiState.value.ottPriorityFilter,
                status = _uiState.value.ottStatusFilter,
                horizonDays = _uiState.value.ottHorizon
            )
            val users = if (user.role == UserRole.ADMIN) repository.listUsers() else emptyList()

            _uiState.update {
                it.copy(
                    scopedDoers = scoped,
                    dashboardMetrics = metrics,
                    recurringTasks = recTasks,
                    oneTimeTasks = ottTasks,
                    usersList = users
                )
            }
        }
    }

    // DASHBOARD FILTERS
    fun setMisMode(mode: String) {
        _uiState.update { it.copy(misMode = mode) }
    }

    fun updateDashFilters(type: String, freq: String, doer: String, from: String, to: String) {
        _uiState.update {
            it.copy(
                dashTypeFilter = type,
                dashFreqFilter = freq,
                dashDoerFilter = doer,
                dashFromFilter = from,
                dashToFilter = to,
                detailsPage = 1
            )
        }
        refreshData()
    }

    fun clearDashFilters() {
        _uiState.update {
            it.copy(
                dashTypeFilter = "",
                dashFreqFilter = "",
                dashDoerFilter = "",
                dashFromFilter = "",
                dashToFilter = "",
                detailsPage = 1
            )
        }
        refreshData()
    }

    fun setDetailsPage(page: Int) {
        _uiState.update { it.copy(detailsPage = page) }
    }

    // RECURRING FILTERS
    fun updateRecFilters(search: String, doer: String, freq: String, status: String, group: String, horizon: Int) {
        _uiState.update {
            it.copy(
                recSearch = search,
                recDoerFilter = doer,
                recFreqFilter = freq,
                recStatusFilter = status,
                recGroup = group,
                recHorizon = horizon,
                recPage = 1
            )
        }
        refreshData()
    }

    // ONE-TIME FILTERS
    fun updateOttFilters(search: String, doer: String, priority: String, status: String, group: String, horizon: Int) {
        _uiState.update {
            it.copy(
                ottSearch = search,
                ottDoerFilter = doer,
                ottPriorityFilter = priority,
                ottStatusFilter = status,
                ottGroup = group,
                ottHorizon = horizon,
                ottPage = 1
            )
        }
        refreshData()
    }

    // ASSIGN RECURRING
    fun setAssignMode(mode: String) {
        _uiState.update { it.copy(assignMode = mode) }
    }

    fun updateRecurringForm(task: String? = null, doer: String? = null, freq: String? = null, start: String? = null) {
        _uiState.update { state ->
            val newDoer = doer ?: state.rDoer
            val desig = state.scopedDoers.firstOrNull { it.name.equals(newDoer, ignoreCase = true) }?.designation ?: state.rDesignation
            state.copy(
                rTask = task ?: state.rTask,
                rDoer = newDoer,
                rDesignation = desig,
                rFrequency = freq ?: state.rFrequency,
                rStartDate = start ?: state.rStartDate,
                rMsg = ""
            )
        }
    }

    fun toggleOffDay(dayIndex: Int) {
        _uiState.update { state ->
            val current = state.rOffDays.toMutableSet()
            if (current.contains(dayIndex)) current.remove(dayIndex) else current.add(dayIndex)
            state.copy(rOffDays = current)
        }
    }

    fun submitRecurringTask() {
        val user = _uiState.value.currentUser ?: return
        val s = _uiState.value
        if (s.rTask.isBlank()) {
            _uiState.update { it.copy(rMsg = "Task title is required.", rIsError = true) }
            return
        }
        if (s.rDoer.isBlank()) {
            _uiState.update { it.copy(rMsg = "Please select a doer.", rIsError = true) }
            return
        }
        if (s.rStartDate.isBlank()) {
            _uiState.update { it.copy(rMsg = "Please select a start date.", rIsError = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isAssigningRecurring = true, rMsg = "") }
            val res = repository.assignRecurringTask(
                currentUser = user,
                task = s.rTask,
                doerName = s.rDoer,
                designation = s.rDesignation,
                frequency = s.rFrequency,
                startDateIso = s.rStartDate,
                offDays = s.rOffDays.toList()
            )
            _uiState.update { it.copy(isAssigningRecurring = false) }

            res.fold(
                onSuccess = { count ->
                    _uiState.update {
                        it.copy(
                            rTask = "",
                            rMsg = "Generated $count recurring task(s) successfully!",
                            rIsError = false
                        )
                    }
                    showToast("Generated $count recurring tasks")
                    refreshData()
                },
                onFailure = { err ->
                    _uiState.update { it.copy(rMsg = err.message ?: "Failed to generate tasks", rIsError = true) }
                }
            )
        }
    }

    // ASSIGN ONE-TIME
    fun updateOneTimeForm(details: String? = null, doer: String? = null, priority: String? = null, due: String? = null, file: String? = null) {
        _uiState.update { state ->
            state.copy(
                oDetails = details ?: state.oDetails,
                oDoer = doer ?: state.oDoer,
                oPriority = priority ?: state.oPriority,
                oDueDate = due ?: state.oDueDate,
                oAttachFile = file ?: state.oAttachFile,
                oMsg = ""
            )
        }
    }

    fun submitOneTimeTask() {
        val user = _uiState.value.currentUser ?: return
        val s = _uiState.value
        if (s.oDetails.isBlank()) {
            _uiState.update { it.copy(oMsg = "Task details are required.", oIsError = true) }
            return
        }
        if (s.oDoer.isBlank()) {
            _uiState.update { it.copy(oMsg = "Please select an assignee.", oIsError = true) }
            return
        }
        if (s.oDueDate.isBlank()) {
            _uiState.update { it.copy(oMsg = "Please select a due date.", oIsError = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isAssigningOneTime = true, oMsg = "") }
            val res = repository.assignOneTimeTask(
                currentUser = user,
                taskDetails = s.oDetails,
                assignedTo = s.oDoer,
                priority = s.oPriority,
                dueDateIso = s.oDueDate,
                attachFile = s.oAttachFile
            )
            _uiState.update { it.copy(isAssigningOneTime = false) }

            res.fold(
                onSuccess = { taskId ->
                    _uiState.update {
                        it.copy(
                            oDetails = "",
                            oAttachFile = "",
                            oMsg = "Assigned task successfully ($taskId)!",
                            oIsError = false
                        )
                    }
                    showToast("Assigned task ($taskId)")
                    refreshData()
                },
                onFailure = { err ->
                    _uiState.update { it.copy(oMsg = err.message ?: "Failed to assign task", oIsError = true) }
                }
            )
        }
    }

    // COMPLETE TASK
    fun openCompleteDialog(task: TaskItem) {
        _uiState.update { it.copy(activeCompleteTask = task) }
    }

    fun dismissCompleteDialog() {
        _uiState.update { it.copy(activeCompleteTask = null) }
    }

    fun completeTask(remarks: String, attachFile: String) {
        val user = _uiState.value.currentUser ?: return
        val task = _uiState.value.activeCompleteTask ?: return

        viewModelScope.launch {
            val res = repository.completeTask(user, task, remarks, attachFile)
            res.fold(
                onSuccess = {
                    dismissCompleteDialog()
                    showToast("Task completed successfully!")
                    refreshData()
                },
                onFailure = { err ->
                    showToast(err.message ?: "Completion failed")
                }
            )
        }
    }

    // EDIT TASK
    fun openEditDialog(task: TaskItem) {
        _uiState.update { it.copy(activeEditTask = task) }
    }

    fun dismissEditDialog() {
        _uiState.update { it.copy(activeEditTask = null) }
    }

    fun saveEditedTask(newTitle: String, newDoer: String, newPriority: String, newDueDateIso: String, newAttachFile: String, applyToSeries: Boolean) {
        val user = _uiState.value.currentUser ?: return
        val task = _uiState.value.activeEditTask ?: return

        viewModelScope.launch {
            val res = if (task.type == TaskType.RECURRING) {
                repository.editRecurringTask(user, task, newTitle, newDoer, newDueDateIso, applyToSeries)
            } else {
                repository.editOneTimeTask(user, task, newTitle, newDoer, newPriority, newDueDateIso, newAttachFile)
            }

            res.fold(
                onSuccess = {
                    dismissEditDialog()
                    showToast("Task updated successfully")
                    refreshData()
                },
                onFailure = { err ->
                    showToast(err.message ?: "Update failed")
                }
            )
        }
    }

    // DELETE TASK
    fun openDeleteDialog(task: TaskItem) {
        _uiState.update { it.copy(activeDeleteTask = task) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(activeDeleteTask = null) }
    }

    fun confirmDeleteTask() {
        val user = _uiState.value.currentUser ?: return
        val task = _uiState.value.activeDeleteTask ?: return

        viewModelScope.launch {
            val res = if (task.type == TaskType.RECURRING) {
                repository.deleteRecurringSeries(user, task)
            } else {
                repository.deleteOneTimeTask(user, task)
            }

            res.fold(
                onSuccess = {
                    dismissDeleteDialog()
                    showToast("Task deleted successfully")
                    refreshData()
                },
                onFailure = { err ->
                    showToast(err.message ?: "Delete failed")
                }
            )
        }
    }

    // USER MANAGEMENT
    fun updateUserForm(name: String? = null, desig: String? = null, email: String? = null, dept: String? = null, role: UserRole? = null) {
        _uiState.update { state ->
            state.copy(
                uName = name ?: state.uName,
                uDesignation = desig ?: state.uDesignation,
                uEmail = email ?: state.uEmail,
                uDepartment = dept ?: state.uDepartment,
                uRole = role ?: state.uRole,
                uMsg = ""
            )
        }
    }

    fun submitUser() {
        val s = _uiState.value
        if (s.uEmail.isBlank()) {
            _uiState.update { it.copy(uMsg = "Email is required.", uIsError = true) }
            return
        }

        val newUser = User(
            name = if (s.uName.isNotBlank()) s.uName.trim() else s.uEmail.substringBefore("@"),
            designation = s.uDesignation.trim(),
            email = s.uEmail.trim(),
            department = s.uDepartment.trim(),
            role = s.uRole
        )

        viewModelScope.launch {
            val res = repository.addUser(newUser)
            res.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            uName = "",
                            uDesignation = "",
                            uEmail = "",
                            uDepartment = "",
                            uMsg = "User added successfully!",
                            uIsError = false
                        )
                    }
                    showToast("User added")
                    refreshData()
                },
                onFailure = { err ->
                    _uiState.update { it.copy(uMsg = err.message ?: "Failed to add user", uIsError = true) }
                }
            )
        }
    }

    fun changeUserRole(rowNum: Int, role: UserRole) {
        viewModelScope.launch {
            repository.updateUserRole(rowNum, role)
            showToast("Role updated")
            refreshData()
        }
    }

    fun deleteUser(rowNum: Int) {
        viewModelScope.launch {
            repository.deleteUser(rowNum)
            showToast("User removed")
            refreshData()
        }
    }

    fun updateSettings(notifyAssign: Boolean? = null, notifyComp: Boolean? = null, pendingRem: Boolean? = null, hour: Int? = null, emails: String? = null) {
        _uiState.update { state ->
            val cur = state.settings
            state.copy(
                settings = cur.copy(
                    notifyOnAssign = notifyAssign ?: cur.notifyOnAssign,
                    notifyOnComplete = notifyComp ?: cur.notifyOnComplete,
                    pendingReminders = pendingRem ?: cur.pendingReminders,
                    reminderHour = hour ?: cur.reminderHour,
                    reminderEmails = emails ?: cur.reminderEmails
                ),
                settingsMsg = ""
            )
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            val res = repository.saveSettings(_uiState.value.settings)
            res.fold(
                onSuccess = {
                    _uiState.update { it.copy(settingsMsg = "Settings saved successfully!") }
                    showToast("Notification settings saved")
                },
                onFailure = { err ->
                    _uiState.update { it.copy(settingsMsg = err.message ?: "Failed to save settings") }
                }
            )
        }
    }
}
