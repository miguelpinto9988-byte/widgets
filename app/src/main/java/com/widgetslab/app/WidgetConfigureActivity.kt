package com.widgetslab.app

import android.app.Activity
import android.appwidget.AppWidgetManager
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
        setContent {
            MaterialTheme {
                Picker { spec, zone ->
                    val store = WidgetStore(this)
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
}

@Composable
private fun Picker(onSelect: (WidgetSpec, String?) -> Unit) {
    var clockSpec by remember { mutableStateOf<WidgetSpec?>(null) }
    var chosenZone by remember { mutableStateOf("UTC") }
    var query by remember { mutableStateOf("") }
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
            Button(onClick = { onSelect(clockSpec!!, chosenZone) },
                modifier = Modifier.fillMaxWidth()) { Text("Añadir reloj") }
            Button(onClick = { clockSpec = null }) { Text("Volver") }
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
                        clockSpec = spec
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
