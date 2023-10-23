package com.example.stromprisapp.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import com.example.stromprisapp.PriceData
import com.example.stromprisapp.Utils
import com.example.stromprisapp.ui.Global.valgtSone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun fetchResult(year: String, month: String, day: String): List<PriceData>? {

        val r = produceState<List<PriceData>?>(initialValue = null) {
            println(valgtSone.toString() + " her er zone")
            value = withContext(Dispatchers.IO) {
                Utils.fetchApiData(year, month, day, valgtSone)
            }
        }
        return r.value;


}

