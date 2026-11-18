package com.widgetslab.app

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.random.Random

/** Reglas puras y comprobables para widgets locales; no necesitan Internet. */
object WidgetExtras {
    fun generate(id: String, random: Random = Random.Default): String = when (id) {
        "coin_flip" -> if (random.nextBoolean()) "Cara" else "Cruz"
        "dice_roll" -> "Dado: " + random.nextInt(1, 7)
        "random_100" -> "Número: " + random.nextInt(1, 101)
        else -> "Widget desconocido"
    }

    fun countdown(target: String, today: LocalDate = LocalDate.now()): String {
        val date = runCatching { LocalDate.parse(target.trim()) }.getOrNull()
            ?: return "Configura una fecha (AAAA-MM-DD)"
        val days = ChronoUnit.DAYS.between(today, date)
        return when {
            days > 1 -> "Faltan $days días"
            days == 1L -> "Falta 1 día"
            days == 0L -> "¡Es hoy!"
            days == -1L -> "Hace 1 día"
            else -> "Hace ${-days} días"
        }
    }
}
