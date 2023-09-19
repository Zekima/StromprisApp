package com.example.stromprisapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stromprisapp.DataFetcher
import com.example.stromprisapp.PriceData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen() {
    val fetchResult = produceState<List<PriceData>?>(initialValue = null) {
        value = withContext(Dispatchers.IO) {
            DataFetcher.fetchApiData("2023", "09", "18", "NO1")
        }
    }
    val sortedData = fetchResult.value?.sortedBy { convertTime(it.timeStart) }
    var holder = 0

    if (sortedData != null) {
        holder = getIndex(sortedData)
        var price = sortedData.get(holder)
        Card(
           modifier = Modifier.fillMaxSize(),
           shape = CardDefaults.outlinedShape,
           colors = CardDefaults.cardColors(Color.LightGray)


        )
        {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(600.dp),
                Alignment.Center
                )
            {
                Text(
                    text = "kl: " + convertTime(price.timeStart) + " = " + String.format("%.2f", price.nokPerKwh * 100) + " øre",
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp

                    )
            }

        }
    }


}

@RequiresApi(Build.VERSION_CODES.O)
fun getIndex(list: List<PriceData> ): Int {
    var holder: Int = 0
    val now = LocalTime.now()
    holder = now.hour
    return holder
}



