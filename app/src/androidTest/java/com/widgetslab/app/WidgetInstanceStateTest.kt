package com.widgetslab.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WidgetInstanceStateTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val prefs = context.getSharedPreferences("widgets_data_v1", Context.MODE_PRIVATE)
    private val first = 9123401
    private val second = 9123402

    @After fun cleanup() {
        WidgetStore(context).removeSelection(first)
        WidgetStore(context).removeSelection(second)
        prefs.edit().remove("count_habits").remove("text_random_coin_flip").commit()
    }

    @Test fun counterStartsFromLegacyAndThenIsIndependent() {
        prefs.edit().putInt("count_habits", 7).commit()
        val store = WidgetStore(context)
        assertEquals(7, store.instanceCount(first, "habits"))
        assertEquals(8, store.incrementInstance(first, "habits"))
        assertEquals(7, store.instanceCount(second, "habits"))
        assertEquals(7, store.count("habits"))
        store.removeSelection(first)
        assertEquals(7, store.instanceCount(first, "habits"))
    }

    @Test fun randomResultIsIsolatedAndDeletionClearsIt() {
        prefs.edit().putString("text_random_coin_flip", "Cara").commit()
        val store = WidgetStore(context)
        assertEquals("Cara", store.instanceRandom(first, "coin_flip"))
        store.saveInstanceRandom(first, "Cruz")
        assertEquals("Cruz", store.instanceRandom(first, "coin_flip"))
        assertEquals("Cara", store.instanceRandom(second, "coin_flip"))
        store.removeSelection(first)
        assertEquals("Cara", store.instanceRandom(first, "coin_flip"))
    }
}
