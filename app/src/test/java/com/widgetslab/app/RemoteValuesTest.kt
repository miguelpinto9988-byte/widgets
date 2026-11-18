package com.widgetslab.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class RemoteValuesTest {
    @Test fun acceptsValidMeasurementsIncludingZero() {
        assertEquals(0.0, RemoteValues.requiredNumber(0.0, 0.0, 100.0, "humedad"), 0.0)
        assertEquals(23.5, RemoteValues.requiredNumber(23.5, -100.0, 70.0, "temperatura"), 0.0)
    }

    @Test fun rejectsMissingAndImplausibleMeasurements() {
        listOf(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, -1.0, 101.0)
            .forEach { value ->
                assertThrows(IllegalArgumentException::class.java) {
                    RemoteValues.requiredNumber(value, 0.0, 100.0, "humedad")
                }
            }
    }

    @Test fun rejectsMalformedTimeAndAcceptsValidIsoTime() {
        assertEquals("06:42", RemoteValues.requiredTime("2026-11-17T06:42"))
        listOf("", "missing", "2026-11-17T99:99", "2026-11-17T06:42:00")
            .forEach { value ->
                assertThrows(IllegalArgumentException::class.java) { RemoteValues.requiredTime(value) }
            }
    }
}
