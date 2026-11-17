package com.widgetslab.app

/** Reglas puras para evitar pérdidas de datos cuando un contador alcanza sus límites. */
object WidgetRules {
    fun safeAdd(current: Int, delta: Int): Int =
        (current.toLong() + delta.toLong()).coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()

    /** Rechaza IDs inválidos o inexistentes antes de procesar pulsaciones externas. */
    fun isRegisteredWidget(id: Int, registered: IntArray): Boolean =
        id > 0 && registered.contains(id)
}
