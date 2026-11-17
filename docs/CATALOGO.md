# Catálogo de widgets (68 conceptos)

La interfaz distingue **Disponible**, **Funcionalidad parcial** y **Requiere integración**. Son parciales: reloj analógico, zonas horarias, reloj de destino, calculadora, pronóstico, calidad del aire, UV, amanecer, Pomodoro, cuenta regresiva, pausas y respiración. Esta clasificación no implica que se hayan completado esas funciones.

Cada concepto aparece en la aplicación y puede seleccionarse al añadir **Panel Widgets** al escritorio. **Aparecer en el catálogo no significa tener su integración completa**. Se distinguen las funciones locales, las que requieren permiso, las conectadas a API pública y las pendientes de servicios de terceros.

| Nº | Nombre | Categoría | Tipo | Estado | Fuente o requisito |
|---:|---|---|---|---|---|
| 1 | Lista de tareas | Productividad | `CHECKLIST` | Local | Local: lista editable |
| 2 | Calendario | Productividad | `CALENDAR` | Con permiso | Permiso de calendario |
| 3 | Notas rápidas | Productividad | `NOTE` | Local | Local: texto editable |
| 4 | Pomodoro | Productividad | `TIMER` | Local | Local: temporizador |
| 5 | Seguimiento de hábitos | Productividad | `COUNTER` | Local | Local: contador de hábitos |
| 6 | Próximos eventos | Productividad | `CALENDAR` | Con permiso | Permiso de calendario |
| 7 | Reloj digital | Relojes | `CLOCK` | Local | Hora del dispositivo |
| 8 | Reloj analógico | Relojes | `CLOCK` | Local | Vista digital funcional; esfera pendiente |
| 9 | Zonas horarias | Relojes | `CLOCK` | Local | Hora local; zonas configurables pendientes |
| 10 | Cuenta regresiva | Relojes | `TIMER` | Local | Local: temporizador |
| 11 | Próximas alarmas | Relojes | `INTEGRATION` | Por integrar | Acceso a la aplicación de alarmas |
| 12 | Clima actual | Clima | `NETWORK` | API pública | Open-Meteo; coordenadas configurables |
| 13 | Pronóstico semanal | Clima | `NETWORK` | API pública | Open-Meteo; resumen actual por ahora |
| 14 | Calidad del aire | Clima | `NETWORK` | API pública | Proveedor Open-Meteo por configurar |
| 15 | Índice UV | Clima | `NETWORK` | API pública | Proveedor Open-Meteo por configurar |
| 16 | Amanecer y atardecer | Clima | `NETWORK` | API pública | Proveedor Open-Meteo por configurar |
| 17 | Reproductor musical | Multimedia | `INTEGRATION` | Por integrar | MediaSession de un reproductor |
| 18 | Podcasts | Multimedia | `INTEGRATION` | Por integrar | Proveedor de podcasts |
| 19 | Listas de reproducción | Multimedia | `INTEGRATION` | Por integrar | Biblioteca del reproductor |
| 20 | Galería de fotos | Multimedia | `INTEGRATION` | Por integrar | Photo Picker del sistema |
| 21 | Audiolibros | Multimedia | `INTEGRATION` | Por integrar | Proveedor de audiolibros |
| 22 | Contactos favoritos | Comunicación | `SHORTCUT` | Acceso sistema | Abrir marcador del sistema |
| 23 | Accesos a conversaciones | Comunicación | `SHORTCUT` | Acceso sistema | Abrir aplicación de mensajería |
| 24 | Mensajes de la aplicación | Comunicación | `INTEGRATION` | Por integrar | Backend de mensajería propio |
| 25 | Reuniones | Comunicación | `CALENDAR` | Con permiso | Permiso de calendario |
| 26 | Tarjetas de estudio | Educación | `NOTE` | Local | Nota educativa editable |
| 27 | Horario de clases | Educación | `NOTE` | Local | Texto local editable |
| 28 | Tareas escolares | Educación | `CHECKLIST` | Local | Lista local editable |
| 29 | Palabra del día | Educación | `NOTE` | Local | Texto local editable |
| 30 | Progreso de lectura | Educación | `COUNTER` | Local | Contador local de páginas |
| 31 | Recordatorio de agua | Bienestar | `COUNTER` | Local | Registro manual local |
| 32 | Pausas de descanso | Bienestar | `TIMER` | Local | Temporizador local |
| 33 | Respiración guiada | Bienestar | `TIMER` | Local | Temporizador local |
| 34 | Conteo de pasos | Bienestar | `INTEGRATION` | Por integrar | Sensor y permiso ACTIVITY_RECOGNITION |
| 35 | Resumen de sueño | Bienestar | `INTEGRATION` | Por integrar | Fuente Health Connect autorizada |
| 36 | Conversor de monedas | Finanzas | `NETWORK` | API pública | Frankfurter API; USD/EUR |
| 37 | Gastos personales | Finanzas | `COUNTER` | Local | Contador manual; no datos bancarios |
| 38 | Presupuesto | Finanzas | `COUNTER` | Local | Contador manual; no datos bancarios |
| 39 | Metas de ahorro | Finanzas | `COUNTER` | Local | Contador manual; no datos bancarios |
| 40 | Seguimiento de precios | Finanzas | `INTEGRATION` | Por integrar | API comercial específica |
| 41 | Próximo transporte | Viajes | `INTEGRATION` | Por integrar | Proveedor de transporte GTFS/GTFS-RT |
| 42 | Itinerario | Viajes | `NOTE` | Local | Texto local editable |
| 43 | Tráfico | Viajes | `INTEGRATION` | Por integrar | Proveedor de navegación |
| 44 | Lista de equipaje | Viajes | `CHECKLIST` | Local | Lista local editable |
| 45 | Horario del destino | Viajes | `CLOCK` | Local | Zona horaria configurable pendiente |
| 46 | Luces inteligentes | Hogar | `INTEGRATION` | Por integrar | Integración con plataforma del hogar |
| 47 | Termostato | Hogar | `INTEGRATION` | Por integrar | API del dispositivo |
| 48 | Consumo energético | Hogar | `INTEGRATION` | Por integrar | API del dispositivo |
| 49 | Estado de dispositivos | Hogar | `INTEGRATION` | Por integrar | API del hogar |
| 50 | Rutinas domésticas | Hogar | `INTEGRATION` | Por integrar | API del hogar |
| 51 | Noticias RSS | Información | `INTEGRATION` | Por integrar | URL de feed y parser RSS |
| 52 | Resultados deportivos | Información | `INTEGRATION` | Por integrar | API deportiva |
| 53 | Calendario de partidos | Información | `INTEGRATION` | Por integrar | API deportiva |
| 54 | Titulares temáticos | Información | `INTEGRATION` | Por integrar | API de noticias |
| 55 | Batería | Dispositivo | `DEVICE` | Lectura sistema | Porcentaje del dispositivo |
| 56 | Almacenamiento | Dispositivo | `DEVICE` | Lectura sistema | Espacio libre interno |
| 57 | Calculadora | Herramientas | `NOTE` | Local | Calculadora interactiva pendiente |
| 58 | Conversor de unidades | Herramientas | `CONVERTER` | Local | Kilómetros a millas |
| 59 | Accesos rápidos | Herramientas | `SHORTCUT` | Acceso sistema | Abrir ajustes Android |
| 60 | Frases personalizadas | Personalización | `NOTE` | Local | Texto local editable |
| 61 | Panel de texto grande | Accesibilidad | `NOTE` | Local | Texto local ampliado |
| 62 | Accesos accesibles | Accesibilidad | `SHORTCUT` | Acceso sistema | Atajos a ajustes Android |
| 63 | Resumen inteligente | IA | `INTEGRATION` | Por integrar | LLM/backend y consentimiento |
| 64 | Tarjetas de aprendizaje con IA | IA | `INTEGRATION` | Por integrar | LLM/backend y consentimiento |
| 65 | Lanzar moneda | Juegos | `RANDOM` | Local | Cara o cruz; botón «Lanzar» desde la app o escritorio |
| 66 | Dado virtual | Juegos | `RANDOM` | Local | Número 1 a 6; botón «Lanzar» |
| 67 | Número al azar | Juegos | `RANDOM` | Local | Número 1 a 100; botón «Lanzar» |
| 68 | Días para una fecha | Productividad | `DATE_COUNTDOWN` | Local | Fecha objetivo AAAA-MM-DD y días restantes |

## Estado por implementación

- **Local:** 30 entradas
- **Con permiso:** 3 entradas
- **Por integrar:** 23 entradas
- **API pública:** 6 entradas
- **Acceso sistema:** 4 entradas
- **Lectura sistema:** 2 entradas

## Alcance real

**Completados como funciones de la app:** listas con casillas, contadores manuales persistentes, notas, relojes de hora local, temporizadores en pantalla, conversión km→millas, calculadora de dos operandos, batería, almacenamiento, selector de un panel de escritorio, lectura autorizada de calendario y consultas a servicios públicos (Open-Meteo y Frankfurter).

**Presentación básica o parcial:** el panel del escritorio puede mostrar títulos, registros locales o el último resultado guardado. Los temporizadores solo transcurren mientras la pantalla interna esté abierta; el reloj de escritorio es una instantánea actualizada por el sistema, no un segundero. Variantes especiales como reloj analógico, husos horarios configurables, notificaciones, sincronización y gráficas todavía requieren implementaciones específicas. El convertidor actual es km→millas y el tipo de cambio de ejemplo USD→EUR.

**Pendientes:** fuentes de terceros (música de otras apps, servicios de mensajería, transporte, deportes, hogar inteligente, fotos, IA, etc.) sin una API autorizada y credenciales. La app no presenta datos ficticios para estos casos.

## Tamaños de pantalla

La app usa una cuadrícula Compose adaptativa `GridCells.Adaptive`. El widget del escritorio admite redimensionamiento horizontal y vertical, sujeto al lanzador Android. Deben probarse teléfonos, tabletas, modo horizontal, texto grande y lectores de pantalla.

Consulta [Integraciones](INTEGRACIONES.md) para permisos y conectores y [Fuentes](FUENTES.md) para documentación técnica.

## Nuevas funciones, 17 de noviembre de 2026

Los nuevos generadores se pueden ejecutar desde el panel del escritorio; el último resultado se guarda en el dispositivo. La fecha objetivo se introduce en formato ISO AAAA-MM-DD. Se calcula la diferencia de días en la zona horaria local en el momento de mostrarla.
