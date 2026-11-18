package com.widgetslab.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class ChecklistItem(val title: String, val done: Boolean = false)

class WidgetStore(context: Context) {
    companion object {
        // Shared across WidgetStore instances (launcher receiver and Compose activity).
        private val writeLock = Any()
        // Bound JSON parsing cost before JSONArray allocates objects.
        private const val MAX_CHECKLIST_JSON_CHARS = 256_000
    }

    private val prefs = context.applicationContext.getSharedPreferences("widgets_data_v1", Context.MODE_PRIVATE)

    fun count(id: String): Int = prefs.getInt("count_$id", 0)
    fun increment(id: String, amount: Int = 1): Int = synchronized(writeLock) {
        val value = WidgetRules.safeAdd(count(id), amount)
        prefs.edit().putInt("count_$id", value).apply()
        return value
    }

    fun text(id: String): String = prefs.getString("text_$id", "") ?: ""
    fun saveText(id: String, value: String) {
        prefs.edit().putString("text_$id", value.take(4000)).apply()
    }

    fun list(id: String): List<ChecklistItem> = runCatching {
        val raw = prefs.getString("list_$id", "[]") ?: "[]"
        if (raw.length > MAX_CHECKLIST_JSON_CHARS) return emptyList()
        val array = JSONArray(raw)
        buildList {
            // A corrupt prefix must not hide valid tasks later in the stored list.
            // Cap scanning to bound work on malformed or unexpectedly huge preferences.
            for (index in 0 until minOf(array.length(), 1000)) {
                if (size == 100) break
                val item = array.optJSONObject(index) ?: continue
                val title = item.optString("title", "").trim().take(180)
                if (title.isNotEmpty()) add(ChecklistItem(title, item.optBoolean("done")))
            }
        }
    }.getOrDefault(emptyList())

    fun addItem(id: String, text: String) = synchronized(writeLock) {
        val trimmed = text.trim()
        if (checklistPayloadUnreadable(id)) return
        val items = list(id)
        if (trimmed.isEmpty() || items.size >= 100) return
        saveList(id, items + ChecklistItem(trimmed.take(180)))
    }

    fun toggleItem(id: String, index: Int) = synchronized(writeLock) {
        if (checklistPayloadUnreadable(id)) return
        val items = list(id).toMutableList()
        if (index !in items.indices) return
        items[index] = items[index].copy(done = !items[index].done)
        saveList(id, items)
    }

    fun removeItem(id: String, index: Int) = synchronized(writeLock) {
        if (checklistPayloadUnreadable(id)) return
        val items = list(id)
        if (index !in items.indices) return
        saveList(id, items.filterIndexed { i, _ -> i != index })
    }

    /** Do not destroy a stored checklist that cannot be safely decoded. */
    private fun checklistPayloadUnreadable(id: String): Boolean {
        val raw = prefs.getString("list_$id", null) ?: return false
        if (raw.length > MAX_CHECKLIST_JSON_CHARS) return true
        return runCatching { JSONArray(raw) }.isFailure
    }

    private fun saveList(id: String, items: List<ChecklistItem>) {
        val array = JSONArray()
        items.forEach { array.put(JSONObject().put("title", it.title).put("done", it.done)) }
        prefs.edit().putString("list_$id", array.toString()).apply()
    }

    /** Estado separado por instancia; el valor antiguo es la base para migración. */
    fun instanceCount(widgetId: Int, id: String): Int =
        prefs.getInt("instance_count_$widgetId", count(id))

    fun incrementInstance(widgetId: Int, id: String, amount: Int = 1): Int = synchronized(writeLock) {
        val value = WidgetRules.safeAdd(instanceCount(widgetId, id), amount)
        prefs.edit().putInt("instance_count_$widgetId", value).apply()
        return value
    }

    fun instanceRandom(widgetId: Int, id: String): String =
        prefs.getString("instance_random_$widgetId", null) ?: text("random_" + id)

    fun saveInstanceRandom(widgetId: Int, value: String) = synchronized(writeLock) {
        prefs.edit().putString("instance_random_$widgetId", value.take(4000)).apply()
    }

    /** Zona IANA elegida por tipo; los identificadores desconocidos se rechazan. */
    fun clockZone(id: String): String =
        prefs.getString("clock_zone_$id", "UTC")?.takeIf { it in ClockZones.allowed } ?: "UTC"

    fun saveClockZone(id: String, zone: String): Boolean {
        if (zone !in ClockZones.allowed) return false
        prefs.edit().putString("clock_zone_$id", zone).apply()
        return true
    }

    /** La zona de cada panel prevalece sobre la preferencia histórica por diseño. */
    fun instanceClockZone(widgetId: Int, id: String): String {
        val saved = prefs.getString("instance_clock_zone_$widgetId", null)
        return saved?.takeIf { it in ClockZones.allowed } ?: clockZone(id)
    }

    fun saveInstanceClockZone(widgetId: Int, id: String, zone: String): Boolean {
        if (widgetId < 0 || id !in setOf("timezones", "destination_clock") ||
            zone !in ClockZones.allowed) return false
        synchronized(writeLock) {
            prefs.edit().putString("instance_clock_zone_$widgetId", zone).apply()
        }
        return true
    }

    fun selection(widgetId: Int): String =
        prefs.getString("selected_$widgetId", "tasks") ?: "tasks"

    fun setSelection(widgetId: Int, id: String) = synchronized(writeLock) {
        val selected = WidgetCatalog.byId(id).id
        val editor = prefs.edit().putString("selected_$widgetId", selected)
        if (selection(widgetId) != selected) {
            // Old counter/roll belongs to a different design, not this selection.
            editor.remove("instance_count_$widgetId").remove("instance_random_$widgetId")
                .remove("instance_clock_zone_$widgetId")
        }
        editor.apply()
    }

    fun removeSelection(widgetId: Int) = synchronized(writeLock) {
        prefs.edit()
            .remove("selected_$widgetId")
            .remove("instance_count_$widgetId")
            .remove("instance_random_$widgetId")
            .remove("instance_clock_zone_$widgetId")
            .apply()
    }
}
