package com.widgetslab.app

enum class WidgetMode { CHECKLIST, NOTE, COUNTER, CLOCK, TIMER, NETWORK, DEVICE, CALENDAR, SHORTCUT, CONVERTER, INTEGRATION }

data class WidgetSpec(
    val id: String,
    val title: String,
    val category: String,
    val mode: WidgetMode,
    val requirement: String
) {
    val ready: Boolean get() = mode != WidgetMode.INTEGRATION
    val requiresSetup: Boolean get() = mode == WidgetMode.NETWORK || mode == WidgetMode.CALENDAR || mode == WidgetMode.INTEGRATION
}

/** Catálogo de ideas: cada opción es seleccionable; algunas requieren integración real. */
object WidgetCatalog {
    val all: List<WidgetSpec> = listOf(
        WidgetSpec("tasks", "Lista de tareas", "Productividad", WidgetMode.CHECKLIST, "Local: lista editable"),
        WidgetSpec("calendar", "Calendario", "Productividad", WidgetMode.CALENDAR, "Permiso de calendario"),
        WidgetSpec("notes", "Notas rápidas", "Productividad", WidgetMode.NOTE, "Local: texto editable"),
        WidgetSpec("pomodoro", "Pomodoro", "Productividad", WidgetMode.TIMER, "Local: temporizador"),
        WidgetSpec("habits", "Seguimiento de hábitos", "Productividad", WidgetMode.COUNTER, "Local: contador de hábitos"),
        WidgetSpec("events", "Próximos eventos", "Productividad", WidgetMode.CALENDAR, "Permiso de calendario"),
        WidgetSpec("clock_digital", "Reloj digital", "Relojes", WidgetMode.CLOCK, "Hora del dispositivo"),
        WidgetSpec("clock_analog", "Reloj analógico", "Relojes", WidgetMode.CLOCK, "Vista digital funcional; esfera pendiente"),
        WidgetSpec("timezones", "Zonas horarias", "Relojes", WidgetMode.CLOCK, "Hora local; zonas configurables pendientes"),
        WidgetSpec("countdown", "Cuenta regresiva", "Relojes", WidgetMode.TIMER, "Local: temporizador"),
        WidgetSpec("alarms", "Próximas alarmas", "Relojes", WidgetMode.INTEGRATION, "Acceso a la aplicación de alarmas"),
        WidgetSpec("weather", "Clima actual", "Clima", WidgetMode.NETWORK, "Open-Meteo; coordenadas configurables"),
        WidgetSpec("forecast", "Pronóstico semanal", "Clima", WidgetMode.NETWORK, "Open-Meteo; resumen actual por ahora"),
        WidgetSpec("air_quality", "Calidad del aire", "Clima", WidgetMode.NETWORK, "Proveedor Open-Meteo por configurar"),
        WidgetSpec("uv", "Índice UV", "Clima", WidgetMode.NETWORK, "Proveedor Open-Meteo por configurar"),
        WidgetSpec("sunrise", "Amanecer y atardecer", "Clima", WidgetMode.NETWORK, "Proveedor Open-Meteo por configurar"),
        WidgetSpec("music", "Reproductor musical", "Multimedia", WidgetMode.INTEGRATION, "MediaSession de un reproductor"),
        WidgetSpec("podcasts", "Podcasts", "Multimedia", WidgetMode.INTEGRATION, "Proveedor de podcasts"),
        WidgetSpec("playlists", "Listas de reproducción", "Multimedia", WidgetMode.INTEGRATION, "Biblioteca del reproductor"),
        WidgetSpec("photos", "Galería de fotos", "Multimedia", WidgetMode.INTEGRATION, "Photo Picker del sistema"),
        WidgetSpec("audiobooks", "Audiolibros", "Multimedia", WidgetMode.INTEGRATION, "Proveedor de audiolibros"),
        WidgetSpec("contacts", "Contactos favoritos", "Comunicación", WidgetMode.SHORTCUT, "Abrir marcador del sistema"),
        WidgetSpec("chat_links", "Accesos a conversaciones", "Comunicación", WidgetMode.SHORTCUT, "Abrir aplicación de mensajería"),
        WidgetSpec("messages", "Mensajes de la aplicación", "Comunicación", WidgetMode.INTEGRATION, "Backend de mensajería propio"),
        WidgetSpec("meetings", "Reuniones", "Comunicación", WidgetMode.CALENDAR, "Permiso de calendario"),
        WidgetSpec("flashcards", "Tarjetas de estudio", "Educación", WidgetMode.NOTE, "Nota educativa editable"),
        WidgetSpec("class_schedule", "Horario de clases", "Educación", WidgetMode.NOTE, "Texto local editable"),
        WidgetSpec("homework", "Tareas escolares", "Educación", WidgetMode.CHECKLIST, "Lista local editable"),
        WidgetSpec("word_day", "Palabra del día", "Educación", WidgetMode.NOTE, "Texto local editable"),
        WidgetSpec("reading", "Progreso de lectura", "Educación", WidgetMode.COUNTER, "Contador local de páginas"),
        WidgetSpec("water", "Recordatorio de agua", "Bienestar", WidgetMode.COUNTER, "Registro manual local"),
        WidgetSpec("breaks", "Pausas de descanso", "Bienestar", WidgetMode.TIMER, "Temporizador local"),
        WidgetSpec("breathing", "Respiración guiada", "Bienestar", WidgetMode.TIMER, "Temporizador local"),
        WidgetSpec("steps", "Conteo de pasos", "Bienestar", WidgetMode.INTEGRATION, "Sensor y permiso ACTIVITY_RECOGNITION"),
        WidgetSpec("sleep", "Resumen de sueño", "Bienestar", WidgetMode.INTEGRATION, "Fuente Health Connect autorizada"),
        WidgetSpec("fx", "Conversor de monedas", "Finanzas", WidgetMode.NETWORK, "Frankfurter API; USD/EUR"),
        WidgetSpec("expenses", "Gastos personales", "Finanzas", WidgetMode.COUNTER, "Contador manual; no datos bancarios"),
        WidgetSpec("budget", "Presupuesto", "Finanzas", WidgetMode.COUNTER, "Contador manual; no datos bancarios"),
        WidgetSpec("savings", "Metas de ahorro", "Finanzas", WidgetMode.COUNTER, "Contador manual; no datos bancarios"),
        WidgetSpec("prices", "Seguimiento de precios", "Finanzas", WidgetMode.INTEGRATION, "API comercial específica"),
        WidgetSpec("transit", "Próximo transporte", "Viajes", WidgetMode.INTEGRATION, "Proveedor de transporte GTFS/GTFS-RT"),
        WidgetSpec("itinerary", "Itinerario", "Viajes", WidgetMode.NOTE, "Texto local editable"),
        WidgetSpec("traffic", "Tráfico", "Viajes", WidgetMode.INTEGRATION, "Proveedor de navegación"),
        WidgetSpec("packing", "Lista de equipaje", "Viajes", WidgetMode.CHECKLIST, "Lista local editable"),
        WidgetSpec("destination_clock", "Horario del destino", "Viajes", WidgetMode.CLOCK, "Zona horaria configurable pendiente"),
        WidgetSpec("lights", "Luces inteligentes", "Hogar", WidgetMode.INTEGRATION, "Integración con plataforma del hogar"),
        WidgetSpec("thermostat", "Termostato", "Hogar", WidgetMode.INTEGRATION, "API del dispositivo"),
        WidgetSpec("energy", "Consumo energético", "Hogar", WidgetMode.INTEGRATION, "API del dispositivo"),
        WidgetSpec("devices", "Estado de dispositivos", "Hogar", WidgetMode.INTEGRATION, "API del hogar"),
        WidgetSpec("home_routines", "Rutinas domésticas", "Hogar", WidgetMode.INTEGRATION, "API del hogar"),
        WidgetSpec("rss", "Noticias RSS", "Información", WidgetMode.INTEGRATION, "URL de feed y parser RSS"),
        WidgetSpec("scores", "Resultados deportivos", "Información", WidgetMode.INTEGRATION, "API deportiva"),
        WidgetSpec("matches", "Calendario de partidos", "Información", WidgetMode.INTEGRATION, "API deportiva"),
        WidgetSpec("headlines", "Titulares temáticos", "Información", WidgetMode.INTEGRATION, "API de noticias"),
        WidgetSpec("battery", "Batería", "Dispositivo", WidgetMode.DEVICE, "Porcentaje del dispositivo"),
        WidgetSpec("storage", "Almacenamiento", "Dispositivo", WidgetMode.DEVICE, "Espacio libre interno"),
        WidgetSpec("calculator", "Calculadora", "Herramientas", WidgetMode.NOTE, "Calculadora interactiva pendiente"),
        WidgetSpec("converter", "Conversor de unidades", "Herramientas", WidgetMode.CONVERTER, "Kilómetros a millas"),
        WidgetSpec("shortcuts", "Accesos rápidos", "Herramientas", WidgetMode.SHORTCUT, "Abrir ajustes Android"),
        WidgetSpec("quotes", "Frases personalizadas", "Personalización", WidgetMode.NOTE, "Texto local editable"),
        WidgetSpec("large_text", "Panel de texto grande", "Accesibilidad", WidgetMode.NOTE, "Texto local ampliado"),
        WidgetSpec("accessibility", "Accesos accesibles", "Accesibilidad", WidgetMode.SHORTCUT, "Atajos a ajustes Android"),
        WidgetSpec("ai_summary", "Resumen inteligente", "IA", WidgetMode.INTEGRATION, "LLM/backend y consentimiento"),
        WidgetSpec("ai_study", "Tarjetas de aprendizaje con IA", "IA", WidgetMode.INTEGRATION, "LLM/backend y consentimiento")
    )
    fun byId(id: String?): WidgetSpec = all.firstOrNull { it.id == id } ?: all.first()
    val categories: List<String> = listOf("Todas") + all.map { it.category }.distinct()
}
