package com.widgetslab.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Un proveedor Android nativo que permite crear varias instancias con diferentes widgets. */
class HomeWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { render(context, manager, it) }
    }

    override fun onDeleted(context: Context, ids: IntArray) {
        val store = WidgetStore(context)
        ids.forEach(store::removeSelection)
    }

    companion object {
        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, HomeWidgetProvider::class.java))
            ids.forEach { render(context, manager, it) }
        }

        fun render(context: Context, manager: AppWidgetManager, widgetId: Int) {
            if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return
            val store = WidgetStore(context)
            val spec = WidgetCatalog.byId(store.selection(widgetId))
            val views = RemoteViews(context.packageName, R.layout.widget_panel)

            views.setTextViewText(R.id.widget_title, spec.title)
            val value = when (spec.mode) {
                WidgetMode.COUNTER -> "Registro actual: ${store.instanceCount(widgetId, spec.id)}"
                WidgetMode.RANDOM -> store.instanceRandom(widgetId, spec.id)
                    .ifBlank { "Pulsa Lanzar para generar un resultado" }
                WidgetMode.DATE_COUNTDOWN -> WidgetExtras.countdown(store.text("target_" + spec.id))
                WidgetMode.CHECKLIST -> {
                    val items = store.list(spec.id)
                    "${items.count { it.done }} de ${items.size} elementos completados"
                }
                WidgetMode.NOTE -> store.text(spec.id).ifBlank { "Pulsa para añadir contenido" }
                WidgetMode.CLOCK -> SimpleDateFormat("dd/MM/yyyy  HH:mm",
                    Locale.getDefault()).format(Date())
                WidgetMode.TIMER -> "Inicia el temporizador desde la aplicación"
                WidgetMode.NETWORK -> store.text("cache_" + spec.id)
                    .ifBlank { "Consulta el proveedor desde la aplicación" }
                WidgetMode.CALENDAR -> "Consultar eventos desde la aplicación"
                WidgetMode.DEVICE -> if (spec.id == "battery") {
                    val battery = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                    val level = battery?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                    val scale = battery?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
                    if (level >= 0 && scale > 0) "Batería: ${level * 100 / scale}%" else "Sin datos"
                } else {
                    val stat = StatFs(Environment.getDataDirectory().path)
                    "Libres: %.1f GB".format(stat.availableBytes / 1073741824.0)
                }
                WidgetMode.CONVERTER -> "Convertir kilómetros a millas"
                WidgetMode.SHORTCUT -> "Abrir herramienta"
                WidgetMode.INTEGRATION -> "Necesita integración con un proveedor externo"
            }
            views.setTextViewText(R.id.widget_value, value)
            // TextClock se actualiza con Android; una TextView normal solo cambiaba
            // cada ~30 min, dejando el reloj del escritorio desactualizado.
            val isClock = spec.mode == WidgetMode.CLOCK
            views.setViewVisibility(R.id.widget_value, if (isClock) View.GONE else View.VISIBLE)
            views.setViewVisibility(R.id.widget_live_clock, if (isClock) View.VISIBLE else View.GONE)
            views.setContentDescription(R.id.widget_value, "${spec.title}: $value")

            val openIntent = Intent(context, MainActivity::class.java).apply {
                putExtra("open_spec", spec.id)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            val open = PendingIntent.getActivity(context, widgetId, openIntent, flags)
            views.setOnClickPendingIntent(R.id.widget_title, open)
            views.setOnClickPendingIntent(R.id.widget_value, open)
            views.setOnClickPendingIntent(R.id.widget_live_clock, open)

            if (spec.mode == WidgetMode.COUNTER) {
                val add = Intent(context, WidgetActionReceiver::class.java).apply {
                    action = WidgetActionReceiver.ACTION_INCREMENT
                    data = Uri.parse("widgetslab://counter/$widgetId")
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                }
                views.setTextViewText(R.id.widget_action, "+1")
                views.setContentDescription(R.id.widget_action, "Sumar una unidad a ${spec.title}")
                views.setOnClickPendingIntent(R.id.widget_action,
                    PendingIntent.getBroadcast(context, widgetId, add, flags))
            } else if (spec.mode == WidgetMode.RANDOM) {
                val roll = Intent(context, WidgetActionReceiver::class.java).apply {
                    action = WidgetActionReceiver.ACTION_RANDOM
                    data = Uri.parse("widgetslab://random/$widgetId")
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                }
                views.setTextViewText(R.id.widget_action, "Lanzar")
                views.setContentDescription(R.id.widget_action, "Generar nuevo resultado: ${spec.title}")
                views.setOnClickPendingIntent(R.id.widget_action,
                    PendingIntent.getBroadcast(context, widgetId, roll, flags))
            } else {
                views.setTextViewText(R.id.widget_action, "Abrir")
                views.setContentDescription(R.id.widget_action, "Abrir ${spec.title}")
                views.setOnClickPendingIntent(R.id.widget_action, open)
            }
            manager.updateAppWidget(widgetId, views)
        }
    }
}
