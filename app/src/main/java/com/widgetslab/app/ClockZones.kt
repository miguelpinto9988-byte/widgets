package com.widgetslab.app

/** Zonas IANA predefinidas: sin permisos de ubicación ni dependencia de red. */
object ClockZones {
    val allowed = listOf(
        "UTC", "America/Lima", "America/New_York", "America/Mexico_City",
        "America/Los_Angeles", "America/Bogota", "America/Sao_Paulo",
        "Europe/Madrid", "Europe/London", "Asia/Tokyo", "Asia/Kolkata",
        "Australia/Sydney"
    )
}
