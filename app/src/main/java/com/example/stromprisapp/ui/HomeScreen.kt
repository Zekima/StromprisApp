package com.example.stromprisapp.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stromprisapp.PriceData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SimpleDateFormat", "CoroutineCreationDuringComposition")
@Preview
@Composable
fun HomeScreen() {
    val litenOverskrift = 20.sp; val pris = 50.sp; val valuta = 17.sp
    val datesize = 16.sp; val paddingMellomOverskrifter = 70.dp
    val year =  LocalDate.now().year
    val month = LocalDate.now().month.value
    val day = LocalDateTime.now().dayOfMonth
    val list = fetchResult(year = year.toString(), month = month.toString(), day = day.toString())
    var tekst = getList(list)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        TekstMedBakgrunn(
            backgroundColor = Global.bakgrunnsfarge,
            "Strømpriser",
            modifier = Modifier.padding(top = paddingMellomOverskrifter),
            fontSize = 50.sp
        )
        TekstMedBakgrunn(
            backgroundColor = Global.bakgrunnsfarge,
            "Strømprisen idag",
            modifier = Modifier.padding(top = paddingMellomOverskrifter),
            fontSize = litenOverskrift,
            fontWeight = FontWeight.Bold
        )
        TekstMedBakgrunn(
            backgroundColor = Global.bakgrunnsfarge,
            ""+ SimpleDateFormat("dd/MM/yyyy - hh:mm z").format(Date.from(Instant.now())),
            fontSize = datesize
        )

        Row() {
            var currTime by remember { mutableStateOf(LocalTime.now().hour) }
            println(currTime)
            var holder = currTime
            CoroutineScope(Dispatchers.Default).launch {
                delay(1000)
                holder = LocalTime.now().hour
                println(holder)
                if (holder>currTime) {
                    currTime = holder
                }
                println(currTime)
            }
//            LaunchedEffect(key1 = currTime) {
//                val flowTime = flow<Int> {
//                    emit(LocalTime.now().second)
//                    delay(1000)
//                }
//
//                flowTime.collect {
//                    holder = it
//                    println(holder)
//                    if (holder > dum) {
//                     dum = holder
//                        println(dum)
//                    }
//                }
//            }


            TekstMedBakgrunn(
                backgroundColor = Global.bakgrunnsfarge,
                currTime.toString(),
                //formatNOKToString(list?.get(currTime)?.nokPerKwh),
                fontSize = pris

            )
            TekstMedBakgrunn(
                backgroundColor = Global.bakgrunnsfarge,
                "øre/kWh",
                modifier = Modifier.padding(top = 32.dp),
                fontSize = valuta
            )
        }
        TekstMedBakgrunn(
            backgroundColor = Global.bakgrunnsfarge,
            "Medianpris for gårsdagen",
            modifier = Modifier.padding(top = paddingMellomOverskrifter),
            fontSize = litenOverskrift,
            fontWeight = FontWeight.Bold
        )
        Row() {
            TekstMedBakgrunn(
                backgroundColor = Global.bakgrunnsfarge,
                "139.7",
                fontSize = pris
            )
            TekstMedBakgrunn(
                backgroundColor = Global.bakgrunnsfarge,
                "øre/kWh",
                modifier = Modifier.padding(top = 32.dp),
                fontSize = valuta
            )
        }
    }
}

fun formatNOKToString(d: Double?): String {
    var x = d?.times(100)

    return String.format("%.2f",x)
}

@RequiresApi(Build.VERSION_CODES.O)
fun getList(list: List<PriceData>? ) : String {

    var s: String = ""
    if (list != null) {
        for (x in list) {
            if (x.timeStart < LocalTime.now().toString() || x.timeEnd > LocalTime.now().toString()) {
                s = x.nokPerKwh.toString()
                return s
            }

        }
    }
    return s
}
