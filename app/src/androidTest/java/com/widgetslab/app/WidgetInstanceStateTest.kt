package com.widgetslab.app

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WidgetInstanceStateTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
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

    @Test fun concurrentUpdatesFromSeparateStoresNeverLoseIncrements() {
        val workers = 8
        val increments = 100
        val pool = Executors.newFixedThreadPool(workers)
        val start = CountDownLatch(1)
        try {
            val jobs = (0 until workers).map {
                pool.submit {
                    val independentStore = WidgetStore(context)
                    start.await()
                    repeat(increments) { independentStore.incrementInstance(first, "habits") }
                }
            }
            start.countDown()
            jobs.forEach { it.get(30, TimeUnit.SECONDS) }
            assertEquals(workers * increments, WidgetStore(context).instanceCount(first, "habits"))
            assertEquals(0, WidgetStore(context).instanceCount(second, "habits"))
        } finally {
            pool.shutdownNow()
        }
    }

    @Test fun changingDesignClearsOnlyPreviousInstanceState() {
        val store = WidgetStore(context)
        store.setSelection(first, "habits")
        store.incrementInstance(first, "habits", 5)
        store.saveInstanceRandom(first, "Anterior")
        store.incrementInstance(second, "habits", 3)
        store.setSelection(first, "coin_flip")
        assertEquals(0, store.instanceCount(first, "habits"))
        assertEquals("", store.instanceRandom(first, "coin_flip"))
        assertEquals(3, store.instanceCount(second, "habits"))
        store.saveInstanceRandom(first, "Cara")
        store.setSelection(first, "coin_flip")
        assertEquals("Cara", store.instanceRandom(first, "coin_flip"))
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
