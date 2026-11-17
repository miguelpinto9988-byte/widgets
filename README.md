# Widgets Android

Aplicación Android nativa para teléfonos y tabletas: catálogo de **68 ideas de widgets** y un widget de pantalla de inicio configurable.

## Estado de esta entrega

- Catálogo filtrable de 68 diseños.
- Interfaz adaptable con Jetpack Compose.
- Funciones locales reutilizables (contadores, notas, listas, reloj, temporizador, accesos).
- Widget nativo de pantalla de inicio configurable para seleccionar una categoría.
- Integraciones de red opcionales documentadas; **no todas las 68 categorías tienen proveedor real**.

> Importante: los widgets de deportes en directo, viajes, hogar inteligente, mensajería de terceros, IA y otras fuentes de datos requieren servicios, credenciales o permisos específicos. El catálogo muestra esos requisitos y no simula datos reales.

## Inicio

1. Abrir el proyecto con Android Studio y el JDK 17.
2. Sincronizar el proyecto Gradle con acceso a Google Maven/Maven Central.
3. Ejecutar `app` en Android 8.0+ (API 26), teléfono o tableta.
4. En el lanzador Android, añadir el widget llamado **Panel Widgets** y elegir una de las 68 opciones.

Versión inicial de la aplicación: 0.1.0. Antes de publicar en Play Store se deben ejecutar compilación, pruebas y revisión de permisos en Android Studio.

Documentación: [Catálogo y estado](docs/CATALOGO.md) · [Integraciones](docs/INTEGRACIONES.md) · [Fuentes](docs/FUENTES.md).

Investigación: [App Widgets Android](https://developer.android.com/develop/ui/views/appwidgets/overview) · [Diseño adaptable](https://developer.android.com/develop/adaptive-apps/guides/get-started-with-adaptive-apps).

## Widgets nuevos de esta versión

Se añadieron cuatro widgets originales y funcionales: **Lanzar moneda**, **Dado virtual**, **Número al azar (1–100)** y **Días para una fecha**. Los tres primeros tienen acción directa «Lanzar» desde el escritorio Android; la fecha objetivo se guarda dentro de la aplicación y se muestra en el panel del escritorio. Incluyen pruebas unitarias.

### Estado por instancia del escritorio (en validación)

Los botones **+1** y **Lanzar** del panel Android guardan ahora su resultado por `appWidgetId`: dos paneles del mismo tipo no se pisan entre sí. La primera lectura usa el valor global anterior como base de migración; al retirar un panel se elimina su estado privado. La edición desde la pantalla principal sigue utilizando el dato global del catálogo y **no** modifica automáticamente las instancias ya personalizadas. Los demás modos (notas, listas, fechas) aún comparten datos por tipo; seguimiento en la incidencia #4.

Pruebas propuestas: `WidgetInstanceStateTest` (aislamiento, migración y limpieza), además de los workflows Android CI y emulador. No considerar el cambio listo para publicar hasta que las ejecuciones sean satisfactorias. Referencia técnica: https://developer.android.com/reference/android/appwidget/AppWidgetManager .

## Relojes con zona horaria (en validación)

Los diseños **Zonas horarias** y **Reloj de destino** permiten seleccionar una zona IANA predefinida desde la aplicación; el panel de inicio usa `TextClock` para actualizar la hora sin sondeos. La configuración es persistente por diseño, todavía no por instancia Android. Ver [limitaciones y pruebas](docs/ZONAS_HORARIAS.md). No considerar esta función validada hasta que finalicen las pruebas del PR.
