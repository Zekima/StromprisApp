package com.example.stromprisapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import com.example.stromprisapp.PriceData
import com.example.stromprisapp.Utils
import com.example.stromprisapp.ui.Global.valgtSone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun fetchResult(year: String, month: String, day: String): List<PriceData>? {

       val d = if (day.toInt() < 10) {
            "0" + day
        } else {
            day
        }

    val m = if (month.toInt() < 10) {
        "0" + month
    } else {
        month
    }

        val r = produceState<List<PriceData>?>(initialValue = null) {
            println(valgtSone + " her er zone")
            value = withContext(Dispatchers.IO) {
                Utils.fetchApiData(year, m, d, valgtSone)
            }

        }
        println(r.value.toString())
        return r.value;


}

