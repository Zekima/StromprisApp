package com.example.stromprisapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import com.example.stromprisapp.PriceData
import com.example.stromprisapp.Utils
import com.example.stromprisapp.ui.Global.valgtSone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Henter resultatet for en periode fra API. Formaterer det korrekt i henhold til API
 *
 * @param year Året
 * @param month
 * @param day Dagen
 * @return Listen med PriceData
 */
@Composable
fun fetchResult(year: String, month: String, day: String): List<PriceData>? {
    val d = if (day.toInt() < 10) "0$day" else day
    val m = if (month.toInt() < 10) "0$month" else month

    val r = produceState<List<PriceData>?>(initialValue = null) {
        value = withContext(Dispatchers.IO) {
            Utils.fetchApiData(year, m, d, valgtSone)
        }
    }
    return r.value;
}

