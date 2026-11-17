# Widgets Android

Aplicación Android nativa para teléfonos y tabletas: catálogo de **64 ideas de widgets** y un widget de pantalla de inicio configurable.

## Estado de esta entrega

- Catálogo filtrable de 64 diseños.
- Interfaz adaptable con Jetpack Compose.
- Funciones locales reutilizables (contadores, notas, listas, reloj, temporizador, accesos).
- Widget nativo de pantalla de inicio configurable para seleccionar una categoría.
- Integraciones de red opcionales documentadas; **no todas las 64 categorías tienen proveedor real**.

> Importante: los widgets de deportes en directo, viajes, hogar inteligente, mensajería de terceros, IA y otras fuentes de datos requieren servicios, credenciales o permisos específicos. El catálogo muestra esos requisitos y no simula datos reales.

## Inicio

1. Abrir el proyecto con Android Studio y el JDK 17.
2. Sincronizar el proyecto Gradle con acceso a Google Maven/Maven Central.
3. Ejecutar `app` en Android 8.0+ (API 26), teléfono o tableta.
4. En el lanzador Android, añadir el widget llamado **Panel Widgets** y elegir una de las 64 opciones.

Versión inicial de la aplicación: 0.1.0. Antes de publicar en Play Store se deben ejecutar compilación, pruebas y revisión de permisos en Android Studio.

Documentación: [Catálogo y estado](docs/CATALOGO.md) · [Integraciones](docs/INTEGRACIONES.md) · [Fuentes](docs/FUENTES.md).

Investigación: [App Widgets Android](https://developer.android.com/develop/ui/views/appwidgets/overview) · [Diseño adaptable](https://developer.android.com/develop/adaptive-apps/guides/get-started-with-adaptive-apps).
