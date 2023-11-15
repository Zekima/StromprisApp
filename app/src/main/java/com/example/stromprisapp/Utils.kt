package com.example.stromprisapp

import com.example.stromprisapp.ui.Global
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.TimeZone

/**
 * Lager en connection til URL og henter data derifra med en bufferreader og parser dette om til Json
 * med forskjellige exceptions
 *
 * @param year Året fra valgt data
 * @param month Måneden fra valgt data
 * @param day Dagen fra valgt data
 * @param priceArea Prisområdet fra valgt data
 * @return A list of [PriceData] blir listen over all data fra utvalgt periode
 */
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

    /**
     * Tar en time-date og konertere den til annet format
     *
     * @param timeStart hvilken tid(string) som skal endre
     * @param format Hvilken format du ønsker det i. Eks: hh for time
     * @return Retunerer nytt format. Evt "Invalid date"
     */
    fun convertTime(timeStart: String, format: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
        val date = inputFormat.parse(timeStart) ?: return "Invalid date"

        val outputFormat = SimpleDateFormat(format)
        outputFormat.timeZone = TimeZone.getTimeZone("UTC")

        return outputFormat.format(date)
    }

    /**
     * Finner prisen med MvA, nettleie, strøm
     * @param pricePerKwh prisenPerKwh som en double
     * @return Prisen med alle forskjellige avgifter inkludert
     */
    fun includeFees(pricePerKwh: Double): Double {
        val mvaRate = 1.25
        val nettleiePerKwh = 0.17
        val avgiftPerKwh = 0.0891
        val eurToNokExchangeRate = 11.8
        val currency = getValuta()

        val priceInNok = if (getValuta() == "NOK") pricePerKwh else pricePerKwh * eurToNokExchangeRate

        val totalPrisKrPerKwh = priceInNok + nettleiePerKwh + avgiftPerKwh
        val totalMedMva = totalPrisKrPerKwh * mvaRate

        return when (currency) {
            "EUR" -> (totalMedMva / eurToNokExchangeRate)
            "NOK" -> totalMedMva
            else -> throw IllegalArgumentException("Currency not found: $currency")
        }
    }
    /**
     * @return ser på valutaNok og returnerer "NOK" hvis true
     */
    fun getValuta() : String {
        return if (Global.valutaNOK) "NOK" else "EUR"
    }

    /**
     * @param s blir ønsket sone (NO1) man øsnker å få tilbake i "bokstav" from
     * @return returnerer stedet som string
     */
    fun convertZoneCode(s : String) : String {
        return when(s) {
            "NO1"  -> "Øst-Norge"
            "NO2" -> "Sør-Norge"
            "NO3" -> "Midt-Norge"
            "NO4" -> "Nord-Norge"
            "NO5" -> " Vest-Norge"
            else -> "velg sone"
        }
    }
}
