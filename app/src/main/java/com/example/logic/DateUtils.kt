package com.example.logic

import com.example.data.model.TaskStatus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    private val displayFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.US).apply {
        timeZone = TimeZone.getDefault()
    }
    private val timestampFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US).apply {
        timeZone = TimeZone.getDefault()
    }
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getDefault()
    }
    private val monthYearFormat = SimpleDateFormat("MMM yyyy", Locale.US).apply {
        timeZone = TimeZone.getDefault()
    }

    fun formatDate(date: Date): String = displayFormat.format(date)
    fun formatTimestamp(date: Date): String = timestampFormat.format(date)
    fun formatIso(date: Date): String = isoFormat.format(date)
    fun formatMonthYear(date: Date): String = monthYearFormat.format(date)

    fun parseDate(str: String?): Date? {
        if (str.isNullOrBlank()) return null
        val clean = str.trim()
        val formats = listOf(
            displayFormat,
            SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US),
            SimpleDateFormat("dd/MM/yyyy", Locale.US),
            isoFormat,
            SimpleDateFormat("yyyy/MM/dd", Locale.US)
        )
        for (fmt in formats) {
            try {
                return fmt.parse(clean)
            } catch (_: Exception) {}
        }
        return null
    }

    fun startOfDay(date: Date = Date()): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    fun daysBetween(from: Date?, to: Date?): Long {
        if (from == null || to == null) return 0L
        val fromCal = startOfDay(from)
        val toCal = startOfDay(to)
        val diff = toCal.time - fromCal.time
        return diff / (1000L * 60 * 60 * 24)
    }

    fun addDays(date: Date, days: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_YEAR, days)
        return cal.time
    }

    fun addMonths(date: Date, months: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.MONTH, months)
        return cal.time
    }

    /**
     * Checks if a calendar day matches any off-days (0=Sunday, 1=Monday... 6=Saturday)
     */
    private fun isOffDay(date: Date, offDays: List<Int>): Boolean {
        if (offDays.isEmpty()) return false
        val cal = Calendar.getInstance()
        cal.time = date
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // Calendar.SUNDAY is 1 -> 0
        return offDays.contains(dayOfWeek)
    }

    fun adjustForOffDays(date: Date, offDays: List<Int>, frequency: String): Date? {
        if (offDays.isEmpty()) return date
        if (!isOffDay(date, offDays)) return date
        if (frequency.equals("Daily", ignoreCase = true)) return null // Skip off days for Daily
        var current = date
        var guard = 0
        while (isOffDay(current, offDays) && guard < 8) {
            current = addDays(current, -1)
            guard++
        }
        return current
    }

    fun lastWorkingDayOfMonth(lastCalDay: Date, offDays: List<Int>): Date {
        var result = lastCalDay
        var guard = 0
        while (isOffDay(result, offDays) && guard < 8) {
            result = addDays(result, -1)
            guard++
        }
        return result
    }

    /**
     * Generates due dates according to the exact Code.gs HORIZON specification:
     * - Daily: 365 days (off-days skipped)
     * - Weekly: 52
     * - Monthly: 12
     * - Quarterly: 8
     * - End of Month: 12 (last working day of each month)
     * - Half Yearly: 4
     * - Yearly: 2
     */
    fun generateDueDates(frequency: String, startDate: Date, offDays: List<Int>): List<Date> {
        val dates = mutableListOf<Date>()
        val cal = Calendar.getInstance()
        cal.time = startDate
        val baseYear = cal.get(Calendar.YEAR)
        val baseMonth = cal.get(Calendar.MONTH)

        when (frequency) {
            "Daily" -> {
                for (i in 0 until 365) {
                    val d = addDays(startDate, i)
                    val adj = adjustForOffDays(d, offDays, "Daily")
                    if (adj != null) dates.add(adj)
                }
            }
            "Weekly" -> {
                for (i in 0 until 52) {
                    val d = addDays(startDate, i * 7)
                    adjustForOffDays(d, offDays, "Weekly")?.let { dates.add(it) }
                }
            }
            "Monthly" -> {
                for (i in 0 until 12) {
                    val d = addMonths(startDate, i)
                    adjustForOffDays(d, offDays, "Monthly")?.let { dates.add(it) }
                }
            }
            "Quarterly" -> {
                for (i in 0 until 8) {
                    val d = addMonths(startDate, i * 3)
                    adjustForOffDays(d, offDays, "Quarterly")?.let { dates.add(it) }
                }
            }
            "End of Month" -> {
                for (i in 0 until 12) {
                    val c = Calendar.getInstance()
                    c.set(Calendar.YEAR, baseYear)
                    c.set(Calendar.MONTH, baseMonth + i + 1)
                    c.set(Calendar.DAY_OF_MONTH, 1)
                    c.add(Calendar.DAY_OF_MONTH, -1) // Last day of month
                    val lastWorking = lastWorkingDayOfMonth(c.time, offDays)
                    dates.add(lastWorking)
                }
            }
            "Half Yearly" -> {
                for (i in 0 until 4) {
                    val d = addMonths(startDate, i * 6)
                    adjustForOffDays(d, offDays, "Half Yearly")?.let { dates.add(it) }
                }
            }
            "Yearly" -> {
                for (i in 0 until 2) {
                    val c = Calendar.getInstance()
                    c.time = startDate
                    c.add(Calendar.YEAR, i)
                    adjustForOffDays(c.time, offDays, "Yearly")?.let { dates.add(it) }
                }
            }
            else -> {
                // Default fallback to 12 monthly occurrences
                for (i in 0 until 12) {
                    val d = addMonths(startDate, i)
                    adjustForOffDays(d, offDays, frequency)?.let { dates.add(it) }
                }
            }
        }
        return dates
    }

    data class LiveStatusResult(val status: TaskStatus, val delayDays: String)

    fun calculateLiveStatus(dueDateStr: String, completedDateStr: String?, storedStatus: String?): LiveStatusResult {
        val due = parseDate(dueDateStr)
        val comp = parseDate(completedDateStr)
        val today = startOfDay()

        if (comp != null) {
            val delay = if (due != null) daysBetween(due, comp) else 0L
            val status = if (delay > 0) TaskStatus.DELAY else TaskStatus.ON_TIME
            return LiveStatusResult(status, if (delay > 0) delay.toString() else "0")
        }

        if (due != null && today.after(due)) {
            val delay = daysBetween(due, today)
            return LiveStatusResult(TaskStatus.DELAY, delay.toString())
        }

        val parsed = TaskStatus.fromString(storedStatus)
        return LiveStatusResult(parsed, "")
    }

    fun getIsoWeek(date: Date): Int {
        val cal = Calendar.getInstance()
        cal.minimalDaysInFirstWeek = 4
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.time = date
        return cal.get(Calendar.WEEK_OF_YEAR)
    }

    fun getWeekRangeLabel(date: Date): String {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.time = date
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val delta = (dayOfWeek - Calendar.MONDAY + 7) % 7
        cal.add(Calendar.DAY_OF_MONTH, -delta)
        val start = cal.time
        cal.add(Calendar.DAY_OF_MONTH, 6)
        val end = cal.time

        val monthFormat = SimpleDateFormat("MMM d", Locale.US)
        val endFormat = SimpleDateFormat("d", Locale.US)
        val calStart = Calendar.getInstance().apply { time = start }
        val calEnd = Calendar.getInstance().apply { time = end }

        return if (calStart.get(Calendar.MONTH) == calEnd.get(Calendar.MONTH)) {
            "${monthFormat.format(start)} – ${endFormat.format(end)}"
        } else {
            "${monthFormat.format(start)} – ${monthFormat.format(end)}"
        }
    }
}
