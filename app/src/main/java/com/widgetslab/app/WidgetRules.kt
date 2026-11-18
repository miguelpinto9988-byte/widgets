package com.widgetslab.app

/** Reglas puras para evitar pérdidas de datos cuando un contador alcanza sus límites. */
object WidgetRules {
    fun safeAdd(current: Int, delta: Int): Int =
        (current.toLong() + delta.toLong()).coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()

    /**
     * Vincula cada acción al URI que genera HomeWidgetProvider.
     * Evita procesar intents sin destino o dirigidos a otro panel.
     */
    fun matchesActionTarget(action: String?, data: String?, widgetId: Int): Boolean {
        if (widgetId <= 0) return false
        val kind = when (action) {
            WidgetActionReceiver.ACTION_INCREMENT -> "counter"
            WidgetActionReceiver.ACTION_RANDOM -> "random"
            else -> return false
        }
        return data == "widgetslab://$kind/$widgetId"
    }

    /** Rechaza IDs inválidos o inexistentes antes de procesar pulsaciones externas. */
    fun isRegisteredWidget(id: Int, registered: IntArray): Boolean =
        id > 0 && registered.contains(id)
}
