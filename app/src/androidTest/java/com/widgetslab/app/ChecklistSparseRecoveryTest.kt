package com.widgetslab.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChecklistSparseRecoveryTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val prefs get() = context.getSharedPreferences("widgets_data_v1", android.content.Context.MODE_PRIVATE)

    @Test fun validTasksAfterCorruptPrefixAreRecovered() {
        val key = "list_qa_sparse_recovery"
        val data = JSONArray()
        repeat(110) { data.put(JSONObject().put("title", "  ")) }
        repeat(105) { data.put(JSONObject().put("title", "Task $it").put("done", it % 2 == 0)) }
        try {
            prefs.edit().putString(key, data.toString()).commit()
            val items = WidgetStore(context).list("qa_sparse_recovery")
            assertEquals(100, items.size)
            assertEquals("Task 0", items.first().title)
            assertEquals(true, items.first().done)
            assertEquals("Task 99", items.last().title)
        } finally {
            prefs.edit().remove(key).commit()
        }
    }

    @Test fun oversizedCorruptPayloadHasBoundedScan() {
        val key = "list_qa_sparse_limit"
        val data = JSONArray()
        repeat(1000) { data.put(JSONObject().put("title", "")) }
        data.put(JSONObject().put("title", "Outside scan bound"))
        try {
            prefs.edit().putString(key, data.toString()).commit()
            assertEquals(emptyList<ChecklistItem>(), WidgetStore(context).list("qa_sparse_limit"))
        } finally {
            prefs.edit().remove(key).commit()
        }
    }
}
