package com.example.stromprisapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@OptIn(ExperimentalMaterial3Api::class)
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
    val list = listOf<String>("NO1", "NO2","NO3","NO4","NO5")

    if (sortedData != null) {
        holder = getIndex(sortedData)
        var price = sortedData.get(holder)
        Card(
           modifier = Modifier.fillMaxSize(),
           shape = CardDefaults.outlinedShape,
           colors = CardDefaults.cardColors(Color.LightGray)
        )
        {
            Box {
                dropDown(list = list)
            }
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

@ExperimentalMaterial3Api
@Composable
fun dropDown(list: List<String>)
 {
     var isexpanded by remember { androidx.compose.runtime.mutableStateOf(false) }
     var selectedItem by remember { mutableStateOf("") }
     Box(modifier = Modifier
         .fillMaxWidth()
         .height(100.dp),
         contentAlignment = Alignment.Center
     ) {
         ExposedDropdownMenuBox(expanded = isexpanded, onExpandedChange = {isexpanded = it}) {
             TextField(value = selectedItem,
                 onValueChange = {},
                 readOnly = true,
                 trailingIcon = {
                     ExposedDropdownMenuDefaults.TrailingIcon(expanded = isexpanded)
                 },
                 colors = ExposedDropdownMenuDefaults.textFieldColors(),
                 modifier = Modifier.menuAnchor())
             ExposedDropdownMenu(expanded = isexpanded, onDismissRequest = { isexpanded = false }) {
                 list.forEachIndexed { index, element ->
                     DropdownMenuItem(text = { Text(text = list.get(index)) }, onClick = {
                         selectedItem = list.get(index)
                         isexpanded = false
                     }
                     )
                 }
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



