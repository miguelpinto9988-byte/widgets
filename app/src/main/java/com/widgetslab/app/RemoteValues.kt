package com.widgetslab.app

/** Rejects absent, non-finite or implausible provider measurements instead of showing false zeroes. */
object RemoteValues {
    fun requiredNumber(value: Double, min: Double, max: Double, field: String): Double {
        require(value.isFinite() && value in min..max) { "Dato no disponible o inválido: $field" }
        return value
    }

    /** Open-Meteo daily sunrise/sunset use ISO local datetime strings. */
    fun requiredTime(value: String): String {
        require(Regex("""\d{4}-\d{2}-\d{2}T([01]\d|2[0-3]):[0-5]\d""").matches(value)) {
            "Hora remota no disponible"
        }
        return value.substringAfter('T')
    }
}
