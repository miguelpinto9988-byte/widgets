package com.widgetslab.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChecklistPayloadBoundsTest {
    @Test
    fun oversizedJsonDoesNotGetParsedOrOverwrittenByActions() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val id = "qa_oversized_payload"
        val prefs = context.getSharedPreferences("widgets_data_v1", 0)
        val raw = org.json.JSONArray().put(org.json.JSONObject().put("title", "x".repeat(256_001))).toString()
        try {
            prefs.edit().putString("list_$id", raw).commit()
            val store = WidgetStore(context)
            assertTrue(store.list(id).isEmpty())
            store.addItem(id, "Nueva tarea")
            store.toggleItem(id, 0)
            store.removeItem(id, 0)
            assertEquals(raw, prefs.getString("list_$id", null))
        } finally {
            prefs.edit().remove("list_$id").commit()
        }
    }

    @Test
    fun ordinaryChecklistRemainsEditable() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val id = "qa_bounded_normal"
        val prefs = context.getSharedPreferences("widgets_data_v1", 0)
        try {
            prefs.edit().remove("list_$id").commit()
            val store = WidgetStore(context)
            store.addItem(id, "Tarea")
            assertEquals(1, store.list(id).size)
            store.toggleItem(id, 0)
            assertTrue(store.list(id).single().done)
            store.removeItem(id, 0)
            assertTrue(store.list(id).isEmpty())
        } finally {
            prefs.edit().remove("list_$id").commit()
        }
    }
}
