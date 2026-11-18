package com.widgetslab.app

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Android inicia esta pantalla cuando se añade Panel Widgets al escritorio. */
class WidgetConfigureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_CANCELED)
        val widgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        val manager = AppWidgetManager.getInstance(this)
        val info = manager.getAppWidgetInfo(widgetId)
        if (info?.provider != ComponentName(this, HomeWidgetProvider::class.java)) {
            finish()
            return
        }
        val store = WidgetStore(this)
        val editing = intent?.getBooleanExtra(EXTRA_EDIT_CLOCK, false) == true
        val current = store.selection(widgetId)
        val editableClock = editing && current in setOf("timezones", "destination_clock")
        if (editing && !editableClock) {
            finish()
            return
        }
        setContent {
            MaterialTheme {
                Picker(
                    initialClockId = if (editableClock) current else null,
                    initialZone = if (editableClock) store.instanceClockZone(widgetId, current) else "UTC",
                    onCancel = { finish() }
                ) { spec, zone ->
                    store.setSelection(widgetId, spec.id)
                    if (zone != null) store.saveInstanceClockZone(widgetId, spec.id, zone)
                    HomeWidgetProvider.render(this, AppWidgetManager.getInstance(this), widgetId)
                    setResult(Activity.RESULT_OK, Intent().putExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
                    finish()
                }
            }
        }
    }

    companion object {
        const val EXTRA_EDIT_CLOCK = "com.widgetslab.app.EDIT_CLOCK"
    }
}

@Composable
private fun Picker(initialClockId: String?, initialZone: String,
                   onCancel: () -> Unit, onSelect: (WidgetSpec, String?) -> Unit) {
    var clockId by rememberSaveable { mutableStateOf(initialClockId) }
    var chosenZone by rememberSaveable { mutableStateOf(initialZone) }
    var query by rememberSaveable { mutableStateOf("") }
    val clockSpec = clockId?.let(WidgetCatalog::byId)
    val visible = WidgetCatalog.all.filter { it.title.contains(query, ignoreCase = true)
        || it.category.contains(query, ignoreCase = true) }
    if (clockSpec != null) {
        Column(Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Zona horaria de este panel", style = MaterialTheme.typography.headlineSmall)
            Text("Cada panel puede tener su propia zona horaria.")
            LazyColumn(Modifier.weight(1f)) {
                items(ClockZones.allowed) { zone ->
                    ElevatedCard(onClick = { chosenZone = zone },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                        Text((if (zone == chosenZone) "✓ " else "") + zone,
                            modifier = Modifier.padding(12.dp))
                    }
                }
            }
            Button(onClick = { onSelect(clockSpec, chosenZone) },
                modifier = Modifier.fillMaxWidth()) { Text(if (initialClockId != null) "Guardar zona" else "Añadir reloj") }
            Button(onClick = { if (initialClockId != null) onCancel() else clockId = null }) { Text("Volver") }
        }
        return
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Selecciona un widget", style = MaterialTheme.typography.headlineSmall)
        Text("Puedes añadir varios paneles y elegir una función distinta en cada uno.")
        OutlinedTextField(query, onValueChange = { query = it },
            label = { Text("Buscar entre 68") }, modifier = Modifier.fillMaxWidth())
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(visible, key = { it.id }) { spec ->
                ElevatedCard(onClick = {
                    if (spec.id == "timezones" || spec.id == "destination_clock") {
                        clockId = spec.id
                    } else onSelect(spec, null)
                },
                    modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(spec.title, style = MaterialTheme.typography.titleMedium)
                        Text(spec.category + " · " + spec.requirement,
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
