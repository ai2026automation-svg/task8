package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.logic.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Date

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SmartFin Tasks", appName)
    }

    @Test
    fun `recurring generation horizon daily and monthly`() {
        val startDate = DateUtils.parseDate("2026-10-15")
        assertNotNull(startDate)
        val offDays = listOf(0) // Sunday off

        val dailyDueDates = DateUtils.generateDueDates("Daily", startDate!!, offDays)
        // In 365 days, ~52 Sundays are skipped
        assertEquals(true, dailyDueDates.size in 312..314)

        val monthlyDueDates = DateUtils.generateDueDates("Monthly", startDate, offDays)
        assertEquals(12, monthlyDueDates.size)

        val quarterlyDueDates = DateUtils.generateDueDates("Quarterly", startDate, offDays)
        assertEquals(8, quarterlyDueDates.size)

        val eomDueDates = DateUtils.generateDueDates("End of Month", startDate, offDays)
        assertEquals(12, eomDueDates.size)
    }
}
