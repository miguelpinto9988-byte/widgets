package com.widgetslab.app

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class CountdownLanguageTest {
    private val today = LocalDate.of(2026, 11, 18)

    @Test fun futureDayUsesSingular() {
        assertEquals("Falta 1 día", WidgetExtras.countdown("2026-11-19", today))
    }

    @Test fun pastDayUsesSingular() {
        assertEquals("Hace 1 día", WidgetExtras.countdown("2026-11-17", today))
    }

    @Test fun multipleDaysRemainPlural() {
        assertEquals("Faltan 2 días", WidgetExtras.countdown("2026-11-20", today))
        assertEquals("Hace 2 días", WidgetExtras.countdown("2026-11-16", today))
    }

    @Test fun sameDayAndInvalidDateRemainExplicit() {
        assertEquals("¡Es hoy!", WidgetExtras.countdown("2026-11-18", today))
        assertEquals("Configura una fecha (AAAA-MM-DD)", WidgetExtras.countdown("2026-02-30", today))
    }

    @Test fun leapDayAndYearBoundaryAreHandled() {
        assertEquals("Falta 1 día", WidgetExtras.countdown("2024-02-29", LocalDate.of(2024, 2, 28)))
        assertEquals("Falta 1 día", WidgetExtras.countdown("2027-01-01", LocalDate.of(2026, 12, 31)))
    }
}
