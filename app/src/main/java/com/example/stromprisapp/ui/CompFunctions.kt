package com.example.stromprisapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import com.example.stromprisapp.PriceData
import com.example.stromprisapp.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun fetchResult(year: String, month: String, day: String): List<PriceData>? {
    val r = produceState<List<PriceData>?>(initialValue = null) {
        value = withContext(Dispatchers.IO) {
            Utils.fetchApiData(year, month, day, "NO1")
        }
    }
    return r.value;
}