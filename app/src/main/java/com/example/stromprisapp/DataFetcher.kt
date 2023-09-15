package com.example.stromprisapp

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object DataFetcher {
    fun fetchApiData(year: String, month: String, day: String, priceArea: String): String? {
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

            return buffer.toString()

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
}