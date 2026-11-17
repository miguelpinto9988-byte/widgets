package com.widgetslab.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

/** Solo fuentes públicas sin claves. El resto de conectores se documenta, no se simula. */
object RemoteData {
    suspend fun refresh(spec: WidgetSpec, store: WidgetStore): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val value = when (spec.id) {
                "weather", "forecast", "air_quality", "uv", "sunrise" -> weather(spec.id, store)
                "fx" -> currency()
                else -> error("Este widget necesita un proveedor de datos externo configurado")
            }
            store.saveText("cache_" + spec.id, value)
            value
        }
    }

    private fun coordinates(store: WidgetStore): Pair<Double, Double> {
        val latitude = store.text("latitude").toDoubleOrNull()
        val longitude = store.text("longitude").toDoubleOrNull()
        require(latitude != null && latitude in -90.0..90.0 &&
            longitude != null && longitude in -180.0..180.0) {
            "Introduce latitud y longitud válidas antes de consultar el tiempo"
        }
        return latitude to longitude
    }

    private fun weather(kind: String, store: WidgetStore): String {
        val (lat, lon) = coordinates(store)
        if (kind == "air_quality") {
            val j = getJson("https://air-quality-api.open-meteo.com/v1/air-quality?latitude=$lat&longitude=$lon&current=european_aqi")
            val aqi = RemoteValues.requiredNumber(j.getJSONObject("current").optDouble("european_aqi"), 0.0, 500.0, "calidad del aire")
            return "Índice de calidad del aire: ${aqi.toInt()}"
        }
        val args = when(kind) {
            "forecast" -> "daily=temperature_2m_max,temperature_2m_min&timezone=auto"
            "sunrise" -> "daily=sunrise,sunset&timezone=auto"
            "uv" -> "daily=uv_index_max&timezone=auto"
            else -> "current=temperature_2m,relative_humidity_2m"
        }
        val j = getJson("https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&$args")
        return when (kind) {
            "forecast" -> {
                val daily = j.getJSONObject("daily")
                val dates = daily.getJSONArray("time")
                val highs = daily.getJSONArray("temperature_2m_max")
                val lows = daily.getJSONArray("temperature_2m_min")
                (0 until minOf(5, dates.length(), highs.length(), lows.length()).also { require(it > 0) { "No hay pronóstico disponible" } }).joinToString(" · ") {
                    "${dates.getString(it).takeLast(5)}: ${RemoteValues.requiredNumber(lows.optDouble(it), -100.0, 70.0, "mínima").toInt()}–${RemoteValues.requiredNumber(highs.optDouble(it), -100.0, 70.0, "máxima").toInt()}°"
                }
            }
            "sunrise" -> {
                val daily = j.getJSONObject("daily")
                "Salida: ${RemoteValues.requiredTime(daily.getJSONArray("sunrise").getString(0))} · Puesta: ${RemoteValues.requiredTime(daily.getJSONArray("sunset").getString(0))}"
            }
            "uv" -> {
                val valUv = j.getJSONObject("daily").getJSONArray("uv_index_max").optDouble(0)
                "UV máximo de hoy: ${String.format(Locale.US, "%.1f", RemoteValues.requiredNumber(valUv, 0.0, 30.0, "UV"))}"
            }
            else -> {
                val current = j.getJSONObject("current")
                "Temperatura: ${RemoteValues.requiredNumber(current.optDouble("temperature_2m"), -100.0, 70.0, "temperatura").toInt()} °C · Humedad: ${RemoteValues.requiredNumber(current.optDouble("relative_humidity_2m"), 0.0, 100.0, "humedad").toInt()}%"
            }
        }
    }

    private fun currency(): String {
        val rates = getJson("https://api.frankfurter.dev/v1/latest?base=USD&symbols=EUR").getJSONObject("rates")
        return "1 USD = ${String.format(Locale.US, "%.4f", RemoteValues.requiredNumber(rates.getDouble("EUR"), 0.000001, 1000000.0, "tipo de cambio"))} EUR"
    }

    private fun getJson(address: String): JSONObject {
        val connection = URL(address).openConnection() as HttpURLConnection
        return try {
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            connection.setRequestProperty("Accept", "application/json")
            require(connection.responseCode in 200..299) { "Error HTTP ${connection.responseCode}" }
            JSONObject(connection.inputStream.bufferedReader().use { reader ->
                val result = StringBuilder()
                val buffer = CharArray(4096)
                while (true) {
                    val count = reader.read(buffer)
                    if (count < 0) break
                    require(result.length + count <= 262144) { "Respuesta remota demasiado grande" }
                    result.append(buffer, 0, count)
                }
                result.toString()
            })
        } finally {
            connection.disconnect()
        }
    }
}
