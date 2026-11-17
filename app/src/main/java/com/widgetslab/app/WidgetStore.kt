package com.widgetslab.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class ChecklistItem(val title: String, val done: Boolean = false)

class WidgetStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("widgets_data_v1", Context.MODE_PRIVATE)

    fun count(id: String): Int = prefs.getInt("count_$id", 0)
    fun increment(id: String, amount: Int = 1): Int {
        val value = WidgetRules.safeAdd(count(id), amount)
        prefs.edit().putInt("count_$id", value).apply()
        return value
    }

    fun text(id: String): String = prefs.getString("text_$id", "") ?: ""
    fun saveText(id: String, value: String) {
        prefs.edit().putString("text_$id", value.take(4000)).apply()
    }

    fun list(id: String): List<ChecklistItem> = runCatching {
        val array = JSONArray(prefs.getString("list_$id", "[]"))
        (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            ChecklistItem(item.optString("title"), item.optBoolean("done"))
        }
    }.getOrDefault(emptyList())

    fun addItem(id: String, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || list(id).size >= 100) return
        saveList(id, list(id) + ChecklistItem(trimmed.take(180)))
    }

    fun toggleItem(id: String, index: Int) {
        val items = list(id).toMutableList()
        if (index !in items.indices) return
        items[index] = items[index].copy(done = !items[index].done)
        saveList(id, items)
    }

    fun removeItem(id: String, index: Int) {
        saveList(id, list(id).filterIndexed { i, _ -> i != index })
    }

    private fun saveList(id: String, items: List<ChecklistItem>) {
        val array = JSONArray()
        items.forEach { array.put(JSONObject().put("title", it.title).put("done", it.done)) }
        prefs.edit().putString("list_$id", array.toString()).apply()
    }

    /** Estado separado por instancia; el valor antiguo es la base para migración. */
    fun instanceCount(widgetId: Int, id: String): Int =
        prefs.getInt("instance_count_$widgetId", count(id))

    fun incrementInstance(widgetId: Int, id: String, amount: Int = 1): Int {
        val value = WidgetRules.safeAdd(instanceCount(widgetId, id), amount)
        prefs.edit().putInt("instance_count_$widgetId", value).apply()
        return value
    }

    fun instanceRandom(widgetId: Int, id: String): String =
        prefs.getString("instance_random_$widgetId", null) ?: text("random_" + id)

    fun saveInstanceRandom(widgetId: Int, value: String) {
        prefs.edit().putString("instance_random_$widgetId", value.take(4000)).apply()
    }

    fun selection(widgetId: Int): String =
        prefs.getString("selected_$widgetId", "tasks") ?: "tasks"

    fun setSelection(widgetId: Int, id: String) {
        prefs.edit().putString("selected_$widgetId", WidgetCatalog.byId(id).id).apply()
    }

    fun removeSelection(widgetId: Int) {
        prefs.edit()
            .remove("selected_$widgetId")
            .remove("instance_count_$widgetId")
            .remove("instance_random_$widgetId")
            .apply()
    }
}
