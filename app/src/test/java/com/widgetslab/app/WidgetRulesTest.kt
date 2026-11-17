package com.widgetslab.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetRulesTest {
    @Test fun countersDoNotWrapAroundWhenFull() {
        assertEquals(Int.MAX_VALUE, WidgetRules.safeAdd(Int.MAX_VALUE, 1))
        assertEquals(Int.MAX_VALUE, WidgetRules.safeAdd(Int.MAX_VALUE - 1, 100))
    }

    @Test fun countersDoNotGoBelowZero() {
        assertEquals(0, WidgetRules.safeAdd(0, -1))
        assertEquals(0, WidgetRules.safeAdd(3, Int.MIN_VALUE))
    }

    @Test fun countersStillBehaveNormally() {
        assertEquals(11, WidgetRules.safeAdd(10, 1))
        assertEquals(9, WidgetRules.safeAdd(10, -1))
    }

    @Test fun actionsRequireRegisteredWidgetId() {
        val active = intArrayOf(3, 7, 12)
        assertTrue(WidgetRules.isRegisteredWidget(7, active))
        assertFalse(WidgetRules.isRegisteredWidget(0, active))
        assertFalse(WidgetRules.isRegisteredWidget(-1, active))
        assertFalse(WidgetRules.isRegisteredWidget(13, active))
        assertFalse(WidgetRules.isRegisteredWidget(7, intArrayOf()))
    }
}
