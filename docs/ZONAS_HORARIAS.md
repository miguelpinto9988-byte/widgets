# Zonas horarias configurables

Los diseños `timezones` y `destination_clock` permiten seleccionar una zona IANA de una lista local en el diálogo de la aplicación. La preferencia persiste tras cerrar la app y se aplica al `TextClock` del panel Android con `RemoteViews.setString(..., "setTimeZone", ...)`. No requiere ubicación, red ni alarmas periódicas.

**Límite conocido:** la preferencia se comparte entre instancias del mismo diseño; todavía no existe una pantalla de configuración de zona por `appWidgetId`. El reloj mostrado en el diálogo sigue la zona del dispositivo; el panel de inicio usa la zona elegida. El diseño `timezones` muestra una zona seleccionada, no varios husos simultáneos.

Pruebas: `ClockZonePersistenceTest` verifica persistencia, independencia entre tipos y rechazo de identificadores desconocidos. Se requiere Android CI y pruebas instrumentadas verdes antes de fusionar.

Documentación: https://developer.android.com/reference/android/widget/TextClock y https://developer.android.com/reference/android/widget/RemoteViews

## Configuración por instancia (propuesta en PR)

Al añadir un panel `timezones` o `destination_clock` desde el lanzador, el selector ofrece las 12 zonas IANA disponibles. Cada `appWidgetId` guarda su propia zona, que se aplica a `TextClock` en `HomeWidgetProvider`. La preferencia anterior por diseño se mantiene como respaldo para paneles antiguos sin configuración individual. Al cambiar el diseño o eliminar el panel se elimina su zona individual; al seleccionar de nuevo el mismo diseño se conserva.

No se solicitan permisos nuevos ni se realizan consultas de red. El diálogo dentro de la aplicación continúa modificando la preferencia general por diseño, no las zonas individuales. La verificación en emulador está pendiente hasta completar CI del PR.
