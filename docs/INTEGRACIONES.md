# Configuración e integraciones

## Requisitos de compilación

- Android Studio con **JDK 17**, SDK de Android **35** y acceso a Google Maven / Maven Central.
- Android Gradle Plugin **8.7.3**, Gradle **8.9**, Kotlin **2.0.21**; Compose utiliza el plugin Kotlin Compose.
- Abrir el directorio raíz con Android Studio; si no dispone de Gradle instalado, instalar Gradle 8.9 o generar el wrapper con `gradle wrapper --gradle-version 8.9`. El repositorio no incluye `gradle-wrapper.jar`.
- Ejecutar `gradle :app:assembleDebug :app:testDebugUnitTest` o usar el menú de Android Studio.
- APK debug resultante: `app/build/outputs/apk/debug/app-debug.apk`.
- Requiere Android 8.0 o superior (minSdk 26); la interfaz se adapta al tamaño de ventana con Compose.

## Configuraciones implementadas

| Categoría | Activación | Permisos / fuente | Observaciones |
|---|---|---|---|
| Tareas y listas | Añadir elementos y marcar casillas | SharedPreferences local | Editables y visibles en widget externo |
| Notas y texto | Escribir y guardar | SharedPreferences local | No sincroniza entre dispositivos |
| Contadores | Tocar +1 / -1 | SharedPreferences local | El botón +1 también funciona en el escritorio |
| Reloj y cuenta atrás | Abrir la tarjeta | Hora del sistema | Temporizador activo solo dentro de la app |
| Calendario y eventos | Abrir tarjeta → Consultar → conceder permiso | `READ_CALENDAR` | Nunca se consulta sin el permiso |
| Clima, previsión, aire, UV, amanecer | Introducir latitud/longitud manuales → Guardar → Consultar | `INTERNET`; Open-Meteo | Sin localización automática ni petición de GPS; sin API key. Requiere conexión |
| Cambio de divisas | Abrir tarjeta → Consultar | `INTERNET`; Frankfurter | Ejemplo USD/EUR, no es una cotización en tiempo real |
| Batería y almacenamiento | Abrir tarjeta | APIs del sistema | Datos del dispositivo sin permisos peligrosos |
| Calculadora | Introducir dos números y operación | Local | Funciones aritméticas elementales |
| Convertidor | Escribir kilómetros | Local | Convierte a millas |
| Accesos | Abrir tarjeta → Abrir acceso directo | Intent del sistema | Marcador o Ajustes |

Los resultados de red se guardan como **última lectura** en almacenamiento local y pueden aparecer en el panel del escritorio. No se actualizan automáticamente cada pocos segundos. Las coordenadas son compartidas por las cinco tarjetas meteorológicas.

**API de Open-Meteo:** https://open-meteo.com/en/docs

**API de calidad del aire:** https://open-meteo.com/en/docs/air-quality-api

**API de divisas:** https://frankfurter.dev/v1/

En versiones comerciales revisa los términos y requisitos de atribución, disponibilidad y límites de cada proveedor.

## Integraciones por completar

- **Deportes / precios / tráfico / transporte / vuelos / noticias:** definir proveedor autorizado, endpoint, autenticación, cuotas y política de caché. No guardar tokens en el repositorio. Con credenciales reales, preferir intermediario backend y limitar los datos personales.
- **Reproducción musical y podcasts:** implementar AndroidX Media3 + `MediaSession` de un reproductor propio o integración soportada; no se puede controlar arbitrariamente cualquier app.
- **Fotografías:** integrar Photo Picker oficial para seleccionar imágenes con consentimiento del usuario y gestionar permisos URI persistentes.
- **Contactos y conversaciones:** el acceso a contactos o al contenido de otras apps requiere permisos y APIs apropiadas; los accesos directos existentes son marcadores o Ajustes genéricos.
- **Pasos y sueño:** `ACTIVITY_RECOGNITION` para conteo de pasos cuando corresponda; Health Connect con consentimiento y tipos de datos correctos para sueño.
- **Hogar inteligente:** autenticar contra la plataforma elegida (por ejemplo, la API correspondiente de Google Home o Home Assistant), definir entidad y permisos por dispositivo.
- **IA:** configurar proveedor, presupuesto, almacenamiento seguro de claves en servidor y consentimiento explícito antes de procesar datos personales.
- **Reloj analógico, gráficos y husos horarios:** componentes Compose especializados y configuración por usuario. El código inicial de la tarjeta de reloj presenta la hora local digital.
- **Recordatorios fuera de pantalla:** implementar WorkManager, canales de notificaciones y solicitud `POST_NOTIFICATIONS` cuando se active esa función.

Estas integraciones están reflejadas en el catálogo; no se habilitan permisos hasta implementar el comportamiento relacionado.

## Consideraciones de Android

Los widgets de pantalla de inicio funcionan con `RemoteViews` (el proyecto usa esta técnica para un proveedor universal configurable). La pantalla principal utiliza Jetpack Compose. Glance sería una alternativa adecuada para widgets separados y ricos en layouts declarativos, pero no debe confundirse con Compose dentro de la app.

Android limita las actualizaciones periódicas a un mínimo práctico de 30 minutos mediante `updatePeriodMillis`. No implementar contadores de segundos con refrescos constantes del widget de escritorio. Para procesos periódicos más elaborados, añadir WorkManager y respetar Doze y batería.

Desde Android 10 las apps normales no pueden activar/desactivar Wi-Fi directamente. No implementar ajustes restringidos por medio de permisos privilegiados. En su lugar, abrir los paneles de Ajustes correspondientes. Evitar datos personales sensibles en widgets visibles en el escritorio.

## Configuración de producción y pruebas

1. Probar compilación y pruebas unitarias.
2. Comprobar permisos en Android 8, Android 13, Android 15+ y equipos reales.
3. Probar el selector de widgets del lanzador, distintas dimensiones, reinicios y varias instancias de Panel Widgets.
4. Añadir pruebas de UI y accesibilidad; verificar contrastes, texto grande y orientación.
5. Revisar los términos de APIs, privacidad, permisos mínimos, seguridad de datos y licencias de terceros.
6. Añadir firma y credenciales de publicación **fuera del repositorio**; generar una distribución antes de darlo por publicado.

No hay un APK compilado ni firmado en esta entrega. La compatibilidad real debe comprobarse compilando el proyecto.

## Cuatro widgets locales adicionales

- Moneda: genera al azar cara o cruz.
- Dado: genera un entero de 1 a 6.
- Número aleatorio: genera un entero de 1 a 100.
- Cuenta atrás hasta fecha: objetivo persistente en `SharedPreferences`, formato ISO `AAAA-MM-DD`, cálculo con `LocalDate` y `ChronoUnit`.

El botón `Lanzar` se conecta mediante `PendingIntent.getBroadcast` a `HomeWidgetProvider` y actualiza la instancia correspondiente. Sin permisos adicionales, servicios externos, imágenes de terceros ni marcas de agua.
