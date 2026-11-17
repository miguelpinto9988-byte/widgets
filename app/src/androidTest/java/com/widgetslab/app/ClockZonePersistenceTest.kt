package com.widgetslab.app

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClockZonePersistenceTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val prefs = context.getSharedPreferences("widgets_data_v1", Context.MODE_PRIVATE)

    @After fun cleanup() {
        prefs.edit().remove("clock_zone_timezones").remove("clock_zone_destination_clock").commit()
    }

    @Test fun zonesPersistIndependentlyAndRejectUnknownIdentifiers() {
        val first = WidgetStore(context)
        assertTrue(first.saveClockZone("timezones", "Asia/Tokyo"))
        assertTrue(first.saveClockZone("destination_clock", "America/Lima"))
        assertEquals("Asia/Tokyo", WidgetStore(context).clockZone("timezones"))
        assertEquals("America/Lima", WidgetStore(context).clockZone("destination_clock"))
        assertFalse(first.saveClockZone("timezones", "Invalid/Zone"))
        assertEquals("Asia/Tokyo", WidgetStore(context).clockZone("timezones"))
    }

    @Test fun corruptedPreferenceFallsBackToUtc() {
        prefs.edit().putString("clock_zone_timezones", "Invalid/Zone").commit()
        assertEquals("UTC", WidgetStore(context).clockZone("timezones"))
    }
}
