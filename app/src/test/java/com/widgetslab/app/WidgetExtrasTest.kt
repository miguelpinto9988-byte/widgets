package com.widgetslab.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.random.Random

class WidgetExtrasTest {
    @Test fun coinResultsAreValid() {
        val random = Random(13)
        repeat(100) {
            assertTrue(WidgetExtras.generate("coin_flip", random) in listOf("Cara", "Cruz"))
        }
    }

    @Test fun diceResultsAreInRange() {
        val random = Random(41)
        repeat(200) {
            val value = WidgetExtras.generate("dice_roll", random).removePrefix("Dado: ").toInt()
            assertTrue(value in 1..6)
        }
    }

    @Test fun randomNumberIsInRange() {
        val random = Random(71)
        repeat(200) {
            val value = WidgetExtras.generate("random_100", random).removePrefix("Número: ").toInt()
            assertTrue(value in 1..100)
        }
    }

    @Test fun countdownHandlesFutureTodayPastAndInvalidDate() {
        val today = LocalDate.of(2026, 11, 17)
        assertEquals("Faltan 3 días", WidgetExtras.countdown("2026-11-20", today))
        assertEquals("¡Es hoy!", WidgetExtras.countdown("2026-11-17", today))
        assertEquals("Hace 2 días", WidgetExtras.countdown("2026-11-15", today))
        assertEquals("Configura una fecha (AAAA-MM-DD)", WidgetExtras.countdown("2026-99-00", today))
    }
}
