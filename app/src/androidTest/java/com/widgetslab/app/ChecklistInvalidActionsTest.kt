package com.widgetslab.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChecklistInvalidActionsTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val prefs get() = context.getSharedPreferences("widgets_data_v1", android.content.Context.MODE_PRIVATE)

    @Test fun invalidRemovalDoesNotRewriteStoredMalformedEntries() {
        val id = "qa_invalid_remove"
        val key = "list_$id"
        val original = JSONArray()
            .put(JSONObject().put("title", "Keep").put("done", false))
            .put(JSONObject().put("title", " "))
            .toString()
        try {
            prefs.edit().putString(key, original).commit()
            val store = WidgetStore(context)
            store.removeItem(id, -1)
            store.removeItem(id, 99)
            assertEquals(original, prefs.getString(key, null))
            assertEquals(listOf(ChecklistItem("Keep")), store.list(id))
        } finally {
            prefs.edit().remove(key).commit()
        }
    }

    @Test fun validRemovalDeletesOnlySelectedTask() {
        val id = "qa_valid_remove"
        val key = "list_$id"
        try {
            val store = WidgetStore(context)
            prefs.edit().remove(key).commit()
            store.addItem(id, "First")
            store.addItem(id, "Second")
            store.removeItem(id, 0)
            assertEquals(listOf(ChecklistItem("Second")), store.list(id))
        } finally {
            prefs.edit().remove(key).commit()
        }
    }
}
