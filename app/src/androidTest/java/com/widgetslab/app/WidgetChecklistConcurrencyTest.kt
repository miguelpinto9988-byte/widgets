package com.widgetslab.app

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WidgetChecklistConcurrencyTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val id = "qa_checklist_atomic"

    @After fun cleanup() {
        context.getSharedPreferences("widgets_data_v1", Context.MODE_PRIVATE)
            .edit().remove("list_$id").commit()
    }

    @Test fun concurrentAddsFromIndependentStoresPreserveEveryItem() {
        val workers = 8
        val perWorker = 10
        val pool = Executors.newFixedThreadPool(workers)
        val start = CountDownLatch(1)
        try {
            val jobs = (0 until workers).map { worker ->
                pool.submit {
                    val store = WidgetStore(context)
                    start.await()
                    repeat(perWorker) { index -> store.addItem(id, "$worker-$index") }
                }
            }
            start.countDown()
            jobs.forEach { it.get(30, TimeUnit.SECONDS) }
            val items = WidgetStore(context).list(id)
            assertEquals(workers * perWorker, items.size)
            assertEquals(workers * perWorker, items.map { it.title }.toSet().size)
        } finally {
            pool.shutdownNow()
        }
    }

    @Test fun concurrentAddsRespectMaximumOfOneHundred() {
        val workers = 8
        val pool = Executors.newFixedThreadPool(workers)
        val start = CountDownLatch(1)
        try {
            val jobs = (0 until workers).map { worker ->
                pool.submit {
                    val store = WidgetStore(context)
                    start.await()
                    repeat(25) { index -> store.addItem(id, "$worker-$index") }
                }
            }
            start.countDown()
            jobs.forEach { it.get(30, TimeUnit.SECONDS) }
            val items = WidgetStore(context).list(id)
            assertEquals(100, items.size)
            assertEquals(100, items.map { it.title }.toSet().size)
        } finally {
            pool.shutdownNow()
        }
    }

    @Test fun concurrentTogglesOnSameItemDoNotLoseChanges() {
        val store = WidgetStore(context)
        store.addItem(id, "Revisar")
        val workers = 8
        val pool = Executors.newFixedThreadPool(workers)
        val start = CountDownLatch(1)
        try {
            val jobs = (0 until workers).map {
                pool.submit {
                    val independentStore = WidgetStore(context)
                    start.await()
                    repeat(25) { independentStore.toggleItem(id, 0) }
                }
            }
            start.countDown()
            jobs.forEach { it.get(30, TimeUnit.SECONDS) }
            assertEquals(false, store.list(id).single().done)
            assertTrue(store.list(id).single().title == "Revisar")
        } finally {
            pool.shutdownNow()
        }
    }
}
