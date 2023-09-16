package com.example.stromprisapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import com.example.stromprisapp.DataFetcher
import com.example.stromprisapp.PriceData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.TimeZone

@Composable
fun GraphScreen() {
    val fetchResult = produceState<List<PriceData>?>(initialValue = null) {
        value = withContext(Dispatchers.IO) {
            DataFetcher.fetchApiData("2023", "09", "16", "NO1")
        }
    }
    val sortedData = fetchResult.value?.sortedBy { convertTime(it.timeStart) }

    Column {
        if (sortedData != null) {
            sortedData.forEach { priceData ->
                Text(
                    text = "kl: " + convertTime(priceData.timeStart) + ", " + String.format("%.2f", priceData.nokPerKwh * 100) + " øre"
                )
            }
        } else {
            Text("Henter data...")
        }
    }
}

fun convertTime(timeStart: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
    val date = inputFormat.parse(timeStart) ?: return "Invalid date"

    val outputFormat = SimpleDateFormat("HH:mm")
    outputFormat.timeZone = TimeZone.getTimeZone("UTC")

    return outputFormat.format(date)
}






