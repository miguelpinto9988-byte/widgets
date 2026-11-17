package com.widgetslab.app

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent

/**
 * Receptor privado para los botones de los widgets.
 * El sistema invoca este receptor mediante PendingIntents creados por nuestra app.
 * No se exporta: las otras aplicaciones no pueden emitir acciones directas.
 */
class WidgetActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_INCREMENT && intent.action != ACTION_RANDOM) return
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
        )
        val manager = AppWidgetManager.getInstance(context)
        val registered = manager.getAppWidgetIds(
            ComponentName(context, HomeWidgetProvider::class.java)
        )
        if (!WidgetRules.isRegisteredWidget(appWidgetId, registered)) return

        val store = WidgetStore(context)
        val spec = WidgetCatalog.byId(store.selection(appWidgetId))
        when {
            intent.action == ACTION_INCREMENT && spec.mode == WidgetMode.COUNTER ->
                store.incrementInstance(appWidgetId, spec.id)
            intent.action == ACTION_RANDOM && spec.mode == WidgetMode.RANDOM ->
                store.saveInstanceRandom(appWidgetId, WidgetExtras.generate(spec.id))
            else -> return
        }
        HomeWidgetProvider.render(context, manager, appWidgetId)
    }

    companion object {
        const val ACTION_INCREMENT = "com.widgetslab.app.ADD_ONE"
        const val ACTION_RANDOM = "com.widgetslab.app.RANDOMIZE"
    }
}
