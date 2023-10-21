package com.example.stromprisapp

import androidx.compose.runtime.produceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.TimeZone

object Utils {
    fun fetchApiData(year: String, month: String, day: String, priceArea: String): List<PriceData>? {
        val apiUrl = "https://www.hvakosterstrommen.no/api/v1/prices/$year/$month-${day}_$priceArea.json"
        var connection: HttpURLConnection? = null
        var reader: BufferedReader? = null

        try {
            val url = URL(apiUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.connect()

            val stream = connection.inputStream
            reader = BufferedReader(InputStreamReader(stream))

            val buffer = StringBuffer()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                buffer.append("$line\n")
            }

            val json = buffer.toString()
            return Json.decodeFromString<List<PriceData>>(json)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            connection?.disconnect()
            try {
                reader?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun convertTime(timeStart: String, format: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
        val date = inputFormat.parse(timeStart) ?: return "Invalid date"

        val outputFormat = SimpleDateFormat(format)
        outputFormat.timeZone = TimeZone.getTimeZone("UTC")

        return outputFormat.format(date)
    }

    fun includeFees(grunnprisOrePerKwh: Double): String {
        val mvaRate = 1.25 // 25%
        val nettleiePerKwh = 0.17 // 17 øre/kwh, som er 0.17 kr/kwh
        val avgiftPerKwh = 0.0891 // 8,91 øre/kwh, som er 0.0891 kr/kwh

        // Konverterer grunnpris til kroner
        val grunnprisKrPerKwh = grunnprisOrePerKwh / 100

        // Kalkuler total pris i kroner
        val totalPrisKrPerKwh = grunnprisKrPerKwh + nettleiePerKwh + avgiftPerKwh
        val totalMedMva = totalPrisKrPerKwh * mvaRate

        // Konverterer tilbake til øre
        val totalOrePerKwh = totalMedMva * 100

        return totalOrePerKwh.toString()
    }

}