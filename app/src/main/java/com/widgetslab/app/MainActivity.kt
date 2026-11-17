package com.widgetslab.app

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.provider.CalendarContract
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initial = intent.getStringExtra("open_spec")
        setContent {
            MaterialTheme {
                WidgetLibrary(initial)
            }
        }
    }
}

@Composable
private fun WidgetLibrary(initialId: String?) {
    val context = LocalContext.current
    val store = remember { WidgetStore(context) }
    var search by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("Todas") }
    var selected by remember { mutableStateOf(initialId?.let(WidgetCatalog::byId)) }
    val filtered = WidgetCatalog.all.filter {
        (category == "Todas" || it.category == category) &&
            (it.title.contains(search, ignoreCase = true) || it.category.contains(search, ignoreCase = true))
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text("Widgets Lab", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("68 diseños · Android y tabletas · edición local y conectores", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = search, onValueChange = { search = it },
            label = { Text("Buscar widget") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(WidgetCatalog.categories) { name ->
                FilterChip(selected = category == name, onClick = { category = name },
                    label = { Text(name) })
            }
        }
        Text("${filtered.size} resultados", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 165.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filtered, key = { it.id }) { spec ->
                ElevatedCard(onClick = { selected = spec }) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(spec.category, style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary)
                        Text(spec.title, fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleMedium)
                        Text(spec.requirement, style = MaterialTheme.typography.bodySmall,
                            maxLines = 3)
                        Text(
                            if (spec.ready) "Abrir" else "Requiere integración",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }

    selected?.let { spec ->
        WidgetDialog(spec = spec, store = store,
            onChanged = { HomeWidgetProvider.refreshAll(context) },
            onClose = { selected = null })
    }
}

@Composable
private fun WidgetDialog(
    spec: WidgetSpec,
    store: WidgetStore,
    onChanged: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var version by remember(spec.id) { mutableIntStateOf(0) }
    var note by remember(spec.id) { mutableStateOf(store.text(spec.id)) }
    var itemText by remember(spec.id) { mutableStateOf("") }
    var remoteResult by remember(spec.id) { mutableStateOf(store.text("cache_" + spec.id)) }
    var busy by remember(spec.id) { mutableStateOf(false) }
    var lat by remember { mutableStateOf(store.text("latitude")) }
    var lon by remember { mutableStateOf(store.text("longitude")) }
    var calendarOutput by remember(spec.id) { mutableStateOf("") }
    val calendarPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        calendarOutput = if (it) "Permiso concedido. Pulsa Consultar eventos." else "Acceso al calendario denegado."
    }
    fun updated() { version++; onChanged() }

    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth().heightIn(max = 680.dp)
        ) {
            Column(Modifier.verticalScroll(rememberScrollState()).padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(spec.title, style = MaterialTheme.typography.headlineSmall)
                Text(spec.category + " · " + spec.requirement, style = MaterialTheme.typography.bodyMedium)
                HorizontalDivider()

                when (spec.mode) {
                    WidgetMode.CHECKLIST -> {
                        // version fuerza el recálculo de los elementos después de modificar el almacenamiento
                        val current = remember(version, spec.id) { store.list(spec.id) }
                        if (current.isEmpty()) Text("Lista vacía. Añade el primer elemento.")
                        current.forEachIndexed { index, item ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Checkbox(checked = item.done,
                                        onCheckedChange = { store.toggleItem(spec.id, index); updated() })
                                    Text(item.title, modifier = Modifier.padding(top = 12.dp))
                                }
                                TextButton(onClick = { store.removeItem(spec.id, index); updated() }) {
                                    Text("Quitar")
                                }
                            }
                        }
                        OutlinedTextField(value = itemText, onValueChange = { itemText = it },
                            label = { Text("Nuevo elemento") }, modifier = Modifier.fillMaxWidth())
                        Button(onClick = { store.addItem(spec.id, itemText); itemText = ""; updated() }) {
                            Text("Añadir a la lista")
                        }
                    }
                    WidgetMode.COUNTER -> {
                        val value = version.let { store.count(spec.id) }
                        Text(value.toString(), style = MaterialTheme.typography.displayLarge)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(onClick = { store.increment(spec.id, -1); updated() }) { Text("− 1") }
                            Button(onClick = { store.increment(spec.id); updated() }) { Text("+ 1") }
                        }
                        Text("Registro manual guardado en este dispositivo.")
                    }
                    WidgetMode.RANDOM -> {
                        val current = version.let { store.text("random_" + spec.id) }
                        Text(current.ifBlank { "Pulsa para generar un resultado" },
                            style = MaterialTheme.typography.headlineMedium)
                        Button(onClick = {
                            store.saveText("random_" + spec.id, WidgetExtras.generate(spec.id))
                            updated()
                        }) { Text("Lanzar") }
                        Text("El último resultado se conserva en este dispositivo.")
                    }
                    WidgetMode.DATE_COUNTDOWN -> {
                        var target by remember(spec.id) { mutableStateOf(store.text("target_" + spec.id)) }
                        OutlinedTextField(
                            value = target, onValueChange = { target = it },
                            label = { Text("Fecha objetivo (AAAA-MM-DD)") },
                            modifier = Modifier.fillMaxWidth(), singleLine = true
                        )
                        Text(WidgetExtras.countdown(target),
                            style = MaterialTheme.typography.headlineMedium)
                        Button(onClick = {
                            if (runCatching { java.time.LocalDate.parse(target.trim()) }.isSuccess) {
                                store.saveText("target_" + spec.id, target.trim())
                                updated()
                            }
                        }) { Text("Guardar fecha") }
                        Text("La pantalla de inicio muestra los días restantes; no necesita Internet.")
                    }
                    WidgetMode.NOTE -> {
                        if (spec.id == "calculator") {
                            CalculatorPane()
                        } else {
                            OutlinedTextField(value = note, onValueChange = { note = it },
                                label = { Text("Contenido") }, minLines = 4,
                                modifier = Modifier.fillMaxWidth())
                            Button(onClick = { store.saveText(spec.id, note); updated() }) { Text("Guardar") }
                        }
                    }
                    WidgetMode.CLOCK -> ClockPane()
                    WidgetMode.TIMER -> TimerPane(spec.id)
                    WidgetMode.CONVERTER -> ConverterPane()
                    WidgetMode.DEVICE -> {
                        Text(deviceInfo(context, spec.id), style = MaterialTheme.typography.titleLarge)
                        OutlinedButton(onClick = { updated() }) { Text("Actualizar") }
                    }
                    WidgetMode.CALENDAR -> {
                        Text("Solo se leen eventos tras autorizar acceso al calendario.")
                        Button(onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                                == PackageManager.PERMISSION_GRANTED) {
                                scope.launch {
                                    calendarOutput = withContext(Dispatchers.IO) { readCalendar(context) }
                                }
                            } else {
                                calendarPermission.launch(Manifest.permission.READ_CALENDAR)
                            }
                        }) { Text("Consultar eventos") }
                        Text(calendarOutput)
                    }
                    WidgetMode.NETWORK -> {
                        if (spec.id != "fx") {
                            OutlinedTextField(lat, { lat = it }, label = { Text("Latitud (-90 a 90)") },
                                singleLine = true, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(lon, { lon = it }, label = { Text("Longitud (-180 a 180)") },
                                singleLine = true, modifier = Modifier.fillMaxWidth())
                            OutlinedButton(onClick = {
                                store.saveText("latitude", lat.trim())
                                store.saveText("longitude", lon.trim())
                            }) { Text("Guardar coordenadas") }
                        }
                        Button(enabled = !busy, onClick = {
                            busy = true
                            scope.launch {
                                val result = RemoteData.refresh(spec, store)
                                remoteResult = result.getOrElse { "Error: ${it.message}" }
                                busy = false
                                if (result.isSuccess) onChanged()
                            }
                        }) { Text(if (busy) "Consultando…" else "Consultar fuente pública") }
                        Text(remoteResult.ifBlank { "Sin datos. Consulta para obtener información actual." })
                    }
                    WidgetMode.SHORTCUT -> {
                        Text("Esta acción abre una herramienta compatible del sistema.")
                        Button(onClick = {
                            val intent = if (spec.id == "contacts") {
                                Intent(Intent.ACTION_DIAL)
                            } else {
                                Intent(Settings.ACTION_SETTINGS)
                            }
                            runCatching { context.startActivity(intent) }
                        }) { Text("Abrir acceso directo") }
                    }
                    WidgetMode.INTEGRATION -> {
                        Text("Integración pendiente. No se muestran datos ficticios.")
                        Text("Requisitos: " + spec.requirement)
                        Text("La API, permisos y configuración están descritos en docs/INTEGRACIONES.md.")
                    }
                }
                HorizontalDivider()
                Text("Para poner este diseño en la pantalla de inicio: mantén pulsado el escritorio de Android, añade Panel Widgets y selecciónalo.",
                    style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = onClose) { Text("Cerrar") }
            }
        }
    }
}

@Composable
private fun ClockPane() {
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = Date()
            delay(1000)
        }
    }
    Text(SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(now),
        style = MaterialTheme.typography.displayMedium)
    Text(SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(now))
}

@Composable
private fun TimerPane(id: String) {
    val initial = when (id) {
        "pomodoro" -> 25 * 60
        "breathing" -> 60
        "breaks" -> 5 * 60
        else -> 10 * 60
    }
    var seconds by remember(id) { mutableIntStateOf(initial) }
    var running by remember(id) { mutableStateOf(false) }
    LaunchedEffect(running, seconds) {
        if (running && seconds > 0) {
            delay(1000)
            seconds--
        }
        if (seconds <= 0) running = false
    }
    Text("%02d:%02d".format(seconds / 60, seconds % 60),
        style = MaterialTheme.typography.displayLarge)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { running = !running }) { Text(if (running) "Pausar" else "Iniciar") }
        OutlinedButton(onClick = { running = false; seconds = initial }) { Text("Reiniciar") }
    }
    Text("El temporizador funciona mientras esta pantalla permanezca abierta.")
}

@Composable
private fun ConverterPane() {
    var distance by remember { mutableStateOf("1") }
    val km = distance.toDoubleOrNull()
    OutlinedTextField(value = distance, onValueChange = { distance = it },
        label = { Text("Kilómetros") }, singleLine = true)
    Text(if (km == null) "Introduce un número" else "%.3f millas".format(km * 0.621371))
}

@Composable
private fun CalculatorPane() {
    var a by remember { mutableStateOf("0") }
    var b by remember { mutableStateOf("0") }
    var operator by remember { mutableStateOf("+") }
    OutlinedTextField(a, { a = it }, label = { Text("Número A") }, singleLine = true)
    OutlinedTextField(b, { b = it }, label = { Text("Número B") }, singleLine = true)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("+", "−", "×", "÷").forEach { op ->
            FilterChip(selected = operator == op, onClick = { operator = op }, label = { Text(op) })
        }
    }
    val x = a.toDoubleOrNull()
    val y = b.toDoubleOrNull()
    val result = if (x == null || y == null) "Introduce números válidos" else when (operator) {
        "+" -> (x + y).toString()
        "−" -> (x - y).toString()
        "×" -> (x * y).toString()
        else -> if (y == 0.0) "No se puede dividir por cero" else (x / y).toString()
    }
    Text(result, style = MaterialTheme.typography.titleLarge)
}

private fun deviceInfo(context: Context, id: String): String {
    if (id == "battery") {
        val battery = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = battery?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = battery?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        return if (level < 0 || scale <= 0) "Batería no disponible" else "Batería: ${level * 100 / scale}%"
    }
    val stat = StatFs(Environment.getDataDirectory().path)
    val gb = stat.availableBytes / (1024.0 * 1024.0 * 1024.0)
    return "Almacenamiento libre: %.1f GB".format(gb)
}

private fun readCalendar(context: Context): String = runCatching {
    val now = System.currentTimeMillis()
    val uri = CalendarContract.Instances.CONTENT_URI.buildUpon().also {
        ContentUris.appendId(it, now)
        ContentUris.appendId(it, now + 7L * 24L * 60L * 60L * 1000L)
    }.build()
    val result = mutableListOf<String>()
    context.contentResolver.query(
        uri,
        arrayOf(CalendarContract.Instances.TITLE, CalendarContract.Instances.BEGIN),
        null, null, CalendarContract.Instances.BEGIN + " ASC"
    )?.use { cursor ->
        while (cursor.moveToNext() && result.size < 8) {
            val title = cursor.getString(0) ?: "Evento"
            val whenText = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
                .format(Date(cursor.getLong(1)))
            result.add("$whenText · $title")
        }
    }
    if (result.isEmpty()) "Sin eventos próximos" else result.joinToString("\n")
}.getOrElse { "No se pudo leer el calendario: ${it.message}" }
