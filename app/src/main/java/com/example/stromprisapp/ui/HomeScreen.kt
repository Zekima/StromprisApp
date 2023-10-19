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
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
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
    var currTimeHour by remember { mutableStateOf(LocalTime.now().hour) }
    var currTTimeMinute by remember { mutableStateOf(LocalTime.now().minute)}
    var hourHolder = 0
    var minuteHolder = 0
    var s = formatNOKToString(list?.get(currTimeHour)?.nokPerKwh)
    var median:Double  = 0.0
    println("GETMEDIAN")

    val currentDate = LocalDate.now().toString()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val parsedDate = LocalDate.parse(currentDate, formatter)

    val isFirstDateOfMonth = parsedDate.dayOfMonth == 1
    val isFirstDateOfYear = parsedDate.dayOfYear == 1

    LaunchedEffect(hourHolder) {
        while (true) {
            delay(1000)
            hourHolder = LocalTime.now().hour
            minuteHolder = LocalTime.now().minute
            if (hourHolder>currTimeHour) {
                if (minuteHolder> 2) {
                    currTimeHour = hourHolder
                    s = formatNOKToString(list?.get(currTimeHour)?.nokPerKwh)
                }
            }
        }
    }


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
            ""+ SimpleDateFormat("dd/MM/yyyy - hh:mm").format(Date.from(Instant.now())),
            fontSize = datesize
        )

        Row() {

            TekstMedBakgrunn(
                backgroundColor = Global.bakgrunnsfarge,
                s,
                fontSize = pris
            )
            TekstMedBakgrunn(
                backgroundColor = Global.bakgrunnsfarge,
                "øre/kWh",
                modifier = Modifier.padding(top = 32.dp),
                fontSize = valuta
            )
        }

        if (currTimeHour>14) {
            TekstMedBakgrunn(tekst = "Median pris imorgen",
                modifier = Modifier.padding(top = paddingMellomOverskrifter),
                fontSize = litenOverskrift,
                fontWeight = FontWeight.Bold
            )
            Row() {
                if (isFirstDateOfMonth) {
                    println(12)
                    val list = fetchResult(
                        year.toString(),
                        (month + 1).toString(),
                        getLastDayOfMonth(year.toInt(), (month-1).toInt()).toString()
                    )

                }

                if (isFirstDateOfYear) {
                    println(13)
                    val list = fetchResult(
                        (year+1).toString(),
                        (1).toString(),
                        getLastDayOfMonth((year + 1).toInt(), 1).toString()
                    )
                    median = calcMedian(list)
                }


                println(14)
                val list = fetchResult(
                    (year).toString(),
                    (month).toString(),
                    (day+1).toString()
                )
                if (list != null) {
                    median = calcMedian(list)
                }

                TekstMedBakgrunn(
                    backgroundColor = Global.bakgrunnsfarge,
                    formatNOKToString(median),
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

        TekstMedBakgrunn(
            backgroundColor = Global.bakgrunnsfarge,
            "Medianpris for gårsdagen",
            modifier = Modifier.padding(top = paddingMellomOverskrifter),
            fontSize = litenOverskrift,
            fontWeight = FontWeight.Bold
        )
        Row() {
            if (isFirstDateOfMonth) {
                println(12)
                val list = fetchResult(
                    year.toString(),
                    (month - 1).toString(),
                    getLastDayOfMonth(year.toInt(), (month-1).toInt()).toString()
                )

            }

            if (isFirstDateOfYear) {
                println(13)
                val list = fetchResult(
                    (year-1).toString(),
                    (1).toString(),
                    getLastDayOfMonth((year - 1).toInt(), 1).toString()
                )
                median = calcMedian(list)
            }


            println(14)
            val list = fetchResult(
                (year).toString(),
                (month).toString(),
                (day-1).toString()
            )
            if (list != null) {
                median = calcMedian(list)
            }

            TekstMedBakgrunn(
                backgroundColor = Global.bakgrunnsfarge,
                formatNOKToString(median),
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
fun getLastDayOfMonth(year: Int, month: Int): Int {
    val yearMonth = YearMonth.of(year, month)
    return yearMonth.lengthOfMonth()
    println("!!!!!!!!!!")
}

fun calcMedian(list : List<PriceData>?): Double {
    println("AAAAAAAA")
    if (list != null) {
        if (list.size %2 == 1 ) {
            var value:Double = 0.0
            for (x:PriceData in list) {
                value += x.nokPerKwh
            }
            return (value/2)
        } else {
            var value: Double = list.get(list.size-1).nokPerKwh
            return (value)
        }

    } else {
        return 0.0
    }
}

