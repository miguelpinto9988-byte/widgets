package com.widgetslab.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetCatalogTest {
    @Test fun catalogContains64Items() {
        assertEquals(64, WidgetCatalog.all.size)
    }

    @Test fun everyWidgetHasUniqueId() {
        val ids = WidgetCatalog.all.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test fun everyWidgetHasCategoryAndName() {
        assertTrue(WidgetCatalog.all.all { it.title.isNotBlank() && it.category.isNotBlank() })
    }

    @Test fun unimplementedServicesAreNotMarkedReady() {
        assertFalse(WidgetCatalog.byId("ai_summary").ready)
        assertFalse(WidgetCatalog.byId("scores").ready)
        assertTrue(WidgetCatalog.byId("tasks").ready)
    }

    @Test fun unknownIdFallsBackToSafeDefault() {
        assertEquals("tasks", WidgetCatalog.byId("not-a-widget").id)
    }
}
