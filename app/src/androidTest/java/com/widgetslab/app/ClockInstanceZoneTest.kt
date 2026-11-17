package com.widgetslab.app

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClockInstanceZoneTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val prefs = context.getSharedPreferences("widgets_data_v1", Context.MODE_PRIVATE)
    private val firstId = 810001
    private val secondId = 810002

    @After fun cleanup() {
        WidgetStore(context).removeSelection(firstId)
        WidgetStore(context).removeSelection(secondId)
        prefs.edit().remove("clock_zone_timezones").remove("clock_zone_destination_clock").commit()
    }

    @Test fun differentPanelsKeepIndependentZonesAcrossStoreInstances() {
        val store = WidgetStore(context)
        store.setSelection(firstId, "timezones")
        store.setSelection(secondId, "timezones")
        assertTrue(store.saveInstanceClockZone(firstId, "timezones", "Asia/Tokyo"))
        assertTrue(store.saveInstanceClockZone(secondId, "timezones", "America/Lima"))
        assertEquals("Asia/Tokyo", WidgetStore(context).instanceClockZone(firstId, "timezones"))
        assertEquals("America/Lima", WidgetStore(context).instanceClockZone(secondId, "timezones"))
        assertFalse(store.saveInstanceClockZone(firstId, "timezones", "Invalid/Zone"))
        assertEquals("Asia/Tokyo", store.instanceClockZone(firstId, "timezones"))
    }

    @Test fun migratesFromDesignPreferenceAndCleansUpOnDeleteOrChange() {
        val store = WidgetStore(context)
        assertTrue(store.saveClockZone("timezones", "Europe/Madrid"))
        store.setSelection(firstId, "timezones")
        assertEquals("Europe/Madrid", store.instanceClockZone(firstId, "timezones"))
        assertTrue(store.saveInstanceClockZone(firstId, "timezones", "Asia/Tokyo"))
        store.setSelection(firstId, "timezones")
        assertEquals("Asia/Tokyo", store.instanceClockZone(firstId, "timezones"))
        store.setSelection(firstId, "tasks")
        assertEquals("Europe/Madrid", store.instanceClockZone(firstId, "timezones"))
        store.setSelection(firstId, "timezones")
        assertTrue(store.saveInstanceClockZone(firstId, "timezones", "America/Lima"))
        store.removeSelection(firstId)
        assertEquals("Europe/Madrid", WidgetStore(context).instanceClockZone(firstId, "timezones"))
    }

    @Test fun invalidInstanceOrNonClockDesignCannotStoreZone() {
        val store = WidgetStore(context)
        assertFalse(store.saveInstanceClockZone(-1, "timezones", "UTC"))
        assertFalse(store.saveInstanceClockZone(firstId, "tasks", "UTC"))
    }
}
