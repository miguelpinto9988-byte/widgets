package com.widgetslab.app

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WidgetChecklistRecoveryTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val prefs = context.getSharedPreferences("widgets_data_v1", Context.MODE_PRIVATE)
    private val id = "qa_recovery_checklist"
    private val key = "list_$id"

    @After fun cleanup() {
        prefs.edit().remove(key).commit()
    }

    @Test fun malformedEntryDoesNotDiscardValidTasks() {
        prefs.edit().putString(key,
            """[{"title":"Comprar","done":true},null,42,{"title":"Leer","done":false}]"""
        ).commit()
        assertEquals(
            listOf(ChecklistItem("Comprar", true), ChecklistItem("Leer", false)),
            WidgetStore(context).list(id)
        )
    }

    @Test fun blankAndMissingTitlesAreIgnoredWithoutLosingOtherTasks() {
        prefs.edit().putString(key,
            """[{"title":"   "},{},{"title":"  Conservar  ","done":true}]"""
        ).commit()
        assertEquals(listOf(ChecklistItem("Conservar", true)), WidgetStore(context).list(id))
    }

    @Test fun oversizedStoredListsAndTitlesAreBounded() {
        val entries = (0 until 120).joinToString(",") {
            """{"title":"${"x".repeat(200)}","done":false}"""
        }
        prefs.edit().putString(key, "[$entries]").commit()
        val recovered = WidgetStore(context).list(id)
        assertEquals(100, recovered.size)
        assertTrue(recovered.all { it.title.length == 180 })
    }

    @Test fun entirelyMalformedJsonFallsBackToEmptyList() {
        prefs.edit().putString(key, "{invalid").commit()
        assertTrue(WidgetStore(context).list(id).isEmpty())
    }
}
