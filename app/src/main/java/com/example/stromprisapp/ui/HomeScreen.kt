package com.example.stromprisapp.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.BigDecimal
import java.math.RoundingMode
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
import android.content.Context
import android.content.SharedPreferences
import com.example.stromprisapp.Utils


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SimpleDateFormat", "CoroutineCreationDuringComposition")
@Preview
@Composable
fun HomeScreen() {
    val sharedPrefMva = LocalContext.current.getSharedPreferences("mySharedPrefMva", Context.MODE_PRIVATE)
    val litenOverskrift = 20.sp; val pris = 50.sp; val valuta = 17.sp
    val datesize = 16.sp; val paddingMellomOverskrifter = 30.dp
    var mVa = sharedPrefMva.getBoolean("medMva", false)
    val year =  LocalDate.now().year
    val month = LocalDate.now().month.value
    val day = LocalDateTime.now().dayOfMonth
    val list = fetchResult(year = year.toString(), month = month.toString(), day = day.toString())
    var currTimeHour by remember { mutableStateOf(LocalTime.now().hour) }
    var currTTimeMinute by remember { mutableStateOf(LocalTime.now().minute)}
    var hourHolder = 0
    var minuteHolder = 0
    var median:String  = ""
    var dagensPrisKr = formatNOKToString(list?.get(currTimeHour)?.nokPerKwh)
    val currentDate = LocalDate.now().toString()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val parsedDate = LocalDate.parse(currentDate, formatter)

    val isFirstDateOfMonth = parsedDate.dayOfMonth == 1
    val isFirstDateOfYear = parsedDate.dayOfYear == 1
    var holder = 0





    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        LaunchedEffect(holder) {
            while (!false) {
                delay(1000)
                hourHolder = LocalTime.now().hour
                minuteHolder = LocalTime.now().minute
                holder = LocalTime.now().second
                if (hourHolder>currTimeHour) {
                    if (minuteHolder > 2) {
                        currTimeHour = hourHolder
                        dagensPrisKr = formatNOKToString(list?.get(currTimeHour)?.nokPerKwh)
                    }
                }
            }
        }

        TekstMedBakgrunn(
            tekst = "Strømpriser",
            modifier = Modifier.padding(top = paddingMellomOverskrifter),
            fontSize = 50.sp
        )
        TekstMedBakgrunn(
            tekst = "Strømprisen idag",
            modifier = Modifier.padding(top = paddingMellomOverskrifter),
            fontSize = litenOverskrift,
            fontWeight = FontWeight.Bold
        )
        TekstMedBakgrunn(
            tekst = ""+ SimpleDateFormat("dd/MM/yyyy - hh:mm z").format(Date.from(Instant.now())),
            fontSize = datesize
        )
        Row() {
            TekstMedBakgrunn(
                tekst = if (dagensPrisKr == "nu") dagensPrisKr
                else if (!mVa) getPrisValuta(dagensPrisKr.toDouble())
                else getPrisValuta(dagensPrisKr.toDouble() * 1.25),
                fontSize = pris

            )
            TekstMedBakgrunn(
                tekst = if(Utils.getValuta() == "NOK") "øre/kWh" else "euro/kWh",
                modifier = Modifier.padding(top = 32.dp),
                fontSize = valuta
            )
        }

        if (currTimeHour>=13 && currTTimeMinute>=2) {
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
                    median = formatNOKToString(calcMedian(list))
                }

                if (isFirstDateOfYear) {
                    println(13)
                    val list = fetchResult(
                        (year+1).toString(),
                        (1).toString(),
                        getLastDayOfMonth((year + 1), 1).toString()
                    )
                    median = formatNOKToString(calcMedian(list))
                }


                println(14)
                val list = fetchResult(
                    (year).toString(),
                    (month).toString(),
                    (day+1).toString()
                )
                if (list != null) {
                    median = formatNOKToString(calcMedian(list))
                }

                TekstMedBakgrunn(
                    tekst =  if (median == "nu") median
                    else if(median.isBlank()) ""
                    else if (!mVa) getPrisValuta(median.toDouble())
                    else getPrisValuta(median.toDouble()*1.25),
                    fontSize = pris,
                )
                TekstMedBakgrunn(
                    tekst = if(Utils.getValuta() == "NOK") "øre/kWh" else "euro/kWh",
                    modifier = Modifier.padding(top = 32.dp),
                    fontSize = valuta
                )
            }

        }

        TekstMedBakgrunn(
            tekst = "Medianpris for gårsdagen",
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
                    getLastDayOfMonth(year, (month-1)).toString()
                )
                median = formatNOKToString(calcMedian(list))
            }

            if (isFirstDateOfYear) {
                println(13)
                val list = fetchResult(
                    (year-1).toString(),
                    (1).toString(),
                    getLastDayOfMonth((year - 1), 1).toString()
                )
                median = formatNOKToString(calcMedian(list))
            }


            println(14)
            val list = fetchResult(
                (year).toString(),
                (month).toString(),
                (day-1).toString()
            )
            if (list != null) {
                median = formatNOKToString(calcMedian(list))
            }

            TekstMedBakgrunn(
                tekst = if (median == "nu") median
                else if (median.isBlank()) ""
                else if (!mVa) getPrisValuta(median.toDouble())
                else getPrisValuta(median.toDouble()*1.25),
                fontSize = pris
            )
            TekstMedBakgrunn(
                tekst = if(Utils.getValuta() == "NOK") "øre/kWh" else "euro/kWh",
                modifier = Modifier.padding(top = 32.dp),
                fontSize = valuta
            )
        }
        Row( modifier = Modifier.padding(top = 10.dp) ) {
            mVa = medMvaSwitch(sharedPreferences = sharedPrefMva)
            TekstMedBakgrunn(
                tekst = " med mVa",
                fontSize = datesize,
                modifier = Modifier.padding(top = 15.dp)
            )
        }
    }
}

@Composable
fun medMvaSwitch(sharedPreferences: SharedPreferences) : Boolean {
    val medMva = remember { mutableStateOf(sharedPreferences.getBoolean("medMva", false)) }
    Switch(
        checked = medMva.value,
        onCheckedChange = { newValue ->
            sharedPreferences.edit().putBoolean("medMva", newValue).apply()
            medMva.value = newValue
        }
    )
    return sharedPreferences.getBoolean("medMva", false)
}

fun formatNOKToString(d: Double?): String {
    var x = d?.times(100)

    return String.format("%.2f",x)
}
fun konverterTilEuro(x : Double) : Double {
    return x / 11.84 / 100
}
fun getPrisValuta(x : Double): String {
    val pris = if (Utils.getValuta() == "NOK") x
    else konverterTilEuro(x)

    return if (Utils.getValuta() == "NOK") String.format("%.2f", pris)
    else String.format("%.3f", pris)
}

fun getLastDayOfMonth(year: Int, month: Int): Int {
    val yearMonth =
        YearMonth.of(year, month)
    return yearMonth.lengthOfMonth()
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
            return value
        }

    } else {
        return 0.0
    }
}
