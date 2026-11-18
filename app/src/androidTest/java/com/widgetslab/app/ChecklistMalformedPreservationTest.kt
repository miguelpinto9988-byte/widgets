package com.widgetslab.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChecklistMalformedPreservationTest {
    @Test
    fun malformedJsonSurvivesAllWidgetActions() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = context.getSharedPreferences("widgets_data_v1", 0)
        val id = "qa_malformed_preserve"
        val raw = "[{\"title\":\"incomplete\""
        try {
            prefs.edit().putString("list_$id", raw).commit()
            val store = WidgetStore(context)
            assertTrue(store.list(id).isEmpty())
            store.addItem(id, "Nueva")
            store.toggleItem(id, 0)
            store.removeItem(id, 0)
            assertEquals(raw, prefs.getString("list_$id", null))
        } finally {
            prefs.edit().remove("list_$id").commit()
        }
    }

    @Test
    fun validJsonWithDamagedEntriesStillAllowsEdits() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = context.getSharedPreferences("widgets_data_v1", 0)
        val id = "qa_partial_preserve"
        try {
            prefs.edit().putString("list_$id", "[null,{\"title\":\"Válida\",\"done\":false}]").commit()
            val store = WidgetStore(context)
            assertEquals(1, store.list(id).size)
            store.toggleItem(id, 0)
            assertTrue(store.list(id).single().done)
            store.addItem(id, "Otra")
            assertEquals(2, store.list(id).size)
            store.removeItem(id, 0)
            assertEquals("Otra", store.list(id).single().title)
        } finally {
            prefs.edit().remove("list_$id").commit()
        }
    }
}
