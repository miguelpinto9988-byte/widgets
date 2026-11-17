package com.widgetslab.app

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/** Una difusión que no procede de una instancia activa no modifica registros locales. */
@RunWith(AndroidJUnit4::class)
class WidgetBroadcastSecurityTest {
    @Test fun unregisteredWidgetCannotIncrementCounter() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val store = WidgetStore(context)
        val invalidWidgetId = Int.MAX_VALUE
        store.setSelection(invalidWidgetId, "habits")
        val before = store.count("habits")
        try {
            val intent = Intent(context, WidgetActionReceiver::class.java).apply {
                action = "com.widgetslab.app.ADD_ONE"
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, invalidWidgetId)
            }
            WidgetActionReceiver().onReceive(context, intent)
            assertEquals(before, store.count("habits"))
        } finally {
            store.removeSelection(invalidWidgetId)
        }
    }
}
