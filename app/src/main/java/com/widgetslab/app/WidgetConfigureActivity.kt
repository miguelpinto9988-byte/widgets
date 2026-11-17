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
                Picker { spec ->
                    WidgetStore(this).setSelection(widgetId, spec.id)
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
private fun Picker(onSelect: (WidgetSpec) -> Unit) {
    var query by remember { mutableStateOf("") }
    val visible = WidgetCatalog.all.filter { it.title.contains(query, ignoreCase = true)
        || it.category.contains(query, ignoreCase = true) }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Selecciona un widget", style = MaterialTheme.typography.headlineSmall)
        Text("Puedes añadir varios paneles y elegir una función distinta en cada uno.")
        OutlinedTextField(query, onValueChange = { query = it },
            label = { Text("Buscar entre 64") }, modifier = Modifier.fillMaxWidth())
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(visible, key = { it.id }) { spec ->
                ElevatedCard(onClick = { onSelect(spec) },
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
