package com.widgetslab.app

import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WidgetReceiverSecurityTest {
    @Test
    fun widgetActionReceiverIsNotExported() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val receiver = ComponentName(context, WidgetActionReceiver::class.java)
        val info = context.packageManager.getReceiverInfo(receiver, 0)

        assertNotNull("WidgetActionReceiver must be registered", info)
        assertFalse("Widget actions must not accept external broadcasts", info.exported)
    }
}
