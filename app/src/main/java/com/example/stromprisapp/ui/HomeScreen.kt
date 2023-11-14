package com.example.stromprisapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stromprisapp.PriceData
import com.example.stromprisapp.Utils
import com.example.stromprisapp.ui.theme.RoundedEdgeCardBody
import com.example.stromprisapp.ui.theme.RoundedEdgeCardBodyHorizontal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Date


@SuppressLint("SimpleDateFormat", "CoroutineCreationDuringComposition")
@Preview
@Composable
fun HomeScreen() {
    println("Recompsing")
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val sharedPrefMva = LocalContext.current.getSharedPreferences("mySharedPrefMva", Context.MODE_PRIVATE)
    val litenOverskrift = 18.sp; val pris = 50.sp; val valuta = if(isLandscape)15.sp else 25.sp
    val datesize = 15.sp; val paddingMellomOverskrifter = 10.dp
    var mVa by remember { mutableStateOf(sharedPrefMva.getBoolean("medMva", false))}
    val year =  LocalDate.now().year
    val month = LocalDate.now().month.value
    val day = LocalDateTime.now().dayOfMonth
    val list = fetchResult(year = year.toString(), month = month.toString(), day.toString())
    var textForDate by remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyy - hh:mm z").format(Date.from(Instant.now()))) }
    var currTimeHour by remember { mutableStateOf(9)}
    var hourHolder = 0
    var minuteHolder = 0
    var median by remember { mutableStateOf("")}
    var dagensPrisKr = if (Utils.getValuta() == "NOK") {
        formatValutaToString(list?.get(currTimeHour)?.nokPerKwh)
    } else {
        formatValutaToString(list?.get(currTimeHour)?.eurPerKwh)
    }
    val currentDate = LocalDate.now().toString()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val parsedDate = LocalDate.parse(currentDate, formatter)
    val isFirstDateOfMonth = parsedDate.dayOfMonth == 1
    val isFirstDateOfYear = parsedDate.dayOfYear == 1
    var holder = 0

    DisposableEffect(currTimeHour) {
        val scope = CoroutineScope(Dispatchers.Main)
        val job = scope.launch {
            while (true) {
                println("bob")
                delay(30_000)
                hourHolder = LocalTime.now().hour
                minuteHolder = LocalTime.now().minute
                holder = LocalTime.now().second
                if (hourHolder>currTimeHour) {
                    println(1)
                    if (minuteHolder > 2) {
                        println(2)
                        currTimeHour = hourHolder
                        dagensPrisKr = if (Utils.getValuta() == "NOK") {
                            formatValutaToString(list?.get(currTimeHour)?.nokPerKwh)
                        } else {
                            formatValutaToString(list?.get(currTimeHour)?.eurPerKwh)
                        }
                        textForDate = SimpleDateFormat("dd/MM/yyyy - hh:mm z").format(Date.from(Instant.now()))
                    }
                }
            }
        }
        onDispose {
            job.cancel()
        }
    }
    if(isLandscape) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TekstMedBakgrunn(
                    tekst = "Strømpriser",
                    fontSize = 45.sp
                )
                Divider(color = Color.Black)
            }
            Spacer(modifier = Modifier.padding(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment =  Alignment.CenterVertically
            )
            {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    RoundedEdgeCardBodyHorizontal {
                        TekstMedBakgrunn(
                            tekst = "Strømprisen idag",
                            modifier = Modifier.padding(top = paddingMellomOverskrifter),
                            fontSize = litenOverskrift,
                            fontWeight = FontWeight.Bold
                        )
                        TekstMedBakgrunn(
                            tekst = textForDate,
                            fontSize = datesize
                        )
                        Row()
                        {
                            TekstMedBakgrunn(
                                tekst = if (dagensPrisKr == "nu") dagensPrisKr
                                else if (!mVa) dagensPrisKr
                                else if (mVa) String.format("%.2f", (dagensPrisKr.toDouble() * 1.25))
                                else "ikke funnet",
                                fontSize = pris

                            )

                        }
                        TekstMedBakgrunn(
                            tekst = if (Utils.getValuta() == "NOK") "øre/kWh" else "cent/kWh",
                            fontSize = valuta
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    RoundedEdgeCardBodyHorizontal {
                        TekstMedBakgrunn(
                            tekst = "Medianpris i går",
                            modifier = Modifier.padding(top = paddingMellomOverskrifter),
                            fontSize = litenOverskrift,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            if (isFirstDateOfMonth) {
                                val list = fetchResult(
                                    year.toString(),
                                    (month - 1).toString(),
                                    getLastDayOfMonth(year, (month - 1)).toString()
                                )
                                median = formatValutaToString(calcMedian(list))
                            }

                            if (isFirstDateOfYear) {
                                val list = fetchResult(
                                    (year - 1).toString(),
                                    (1).toString(),
                                    getLastDayOfMonth((year - 1), 1).toString()
                                )
                                median = formatValutaToString(calcMedian(list))
                            }


                            val list = fetchResult(
                                (year).toString(),
                                (month).toString(),
                                (day - 1).toString()
                            )
                            if (list != null) {
                                median = formatValutaToString(calcMedian(list))
                            }

                            TekstMedBakgrunn(
                                tekst = if (median == "nu") median
                                else if (median.isBlank()) ""
                                else if (!mVa) median
                                else String.format("%.2f", (median.toDouble() * 1.25)),
                                fontSize = pris
                            )

                        }
                        TekstMedBakgrunn(
                            tekst = if (Utils.getValuta() == "NOK") "øre/kWh" else "cent/kWh",
                            fontSize = valuta
                        )
                    }
                }

                if (currTimeHour > 8) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        RoundedEdgeCardBodyHorizontal {
                            TekstMedBakgrunn(
                                tekst = "Median pris imorgen",
                                modifier = Modifier.padding(top = paddingMellomOverskrifter),
                                fontSize = litenOverskrift,
                                fontWeight = FontWeight.Bold
                            )

                            Row {
                                if (isFirstDateOfMonth) {
                                    println(12)
                                    val list = fetchResult(
                                        year.toString(),
                                        (month + 1).toString(),
                                        getLastDayOfMonth(year.toInt(), (month - 1).toInt()).toString()
                                    )
                                    median = formatValutaToString(calcMedian(list))
                                }

                                if (isFirstDateOfYear) {

                                    val list = fetchResult(
                                        (year + 1).toString(),
                                        (1).toString(),
                                        getLastDayOfMonth((year + 1), 1).toString()
                                    )
                                    median = formatValutaToString(calcMedian(list))
                                }

                                val list = fetchResult(
                                    (year).toString(),
                                    (month).toString(),
                                    (day + 1).toString()
                                )
                                if (list != null) {
                                    median = formatValutaToString(calcMedian(list))
                                }

                                TekstMedBakgrunn(
                                    tekst = if (median == "nu") median
                                    else if (median.isBlank()) ""
                                    else if (!mVa) median
                                    else String.format("%.2f", (median.toDouble() * 1.25)),
                                    fontSize = pris,
                                )

                            }
                            TekstMedBakgrunn(
                                tekst = if (Utils.getValuta() == "NOK") "øre/kWh" else "cent/kWh",
                                fontSize = valuta
                            )
                        }
                    }
                }
            }

            if (Global.valgtSone != "NO4") {
                Row {
                    mVa = medMvaSwitch(sharedPreferences = sharedPrefMva)
                    TekstMedBakgrunn(
                        tekst = " med mVa",
                        fontSize = datesize,
                        modifier = Modifier.padding(top = 15.dp)
                    )
                }
            } else {
                mVa = false
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TekstMedBakgrunn(
                    tekst = "Strømpriser",
                    modifier = Modifier.padding(top = paddingMellomOverskrifter),
                    fontSize = 35.sp
                )
                Divider(color = Color.Black)
            }

     Spacer(modifier = Modifier.padding(16.dp))
            RoundedEdgeCardBody {
                TekstMedBakgrunn(
                    tekst = "Strømprisen idag",
                    modifier = Modifier.padding(top = paddingMellomOverskrifter),
                    fontSize = litenOverskrift,
                    fontWeight = FontWeight.Bold
                )
                TekstMedBakgrunn(
                    tekst = textForDate,
                    fontSize = datesize
                )


                Row()
                {
                    TekstMedBakgrunn(
                        tekst = if (dagensPrisKr == "nu") dagensPrisKr
                        else if (!mVa) dagensPrisKr
                        else if (mVa) String.format("%.2f", (dagensPrisKr.toDouble() * 1.25))
                        else "ikke funnet",
                        fontSize = pris

                    )
                    TekstMedBakgrunn(
                        tekst = if (Utils.getValuta() == "NOK") "øre/kWh" else "cent/kWh",
                        modifier = Modifier.padding(top = 32.dp),
                        fontSize = valuta
                    )
                }
            }

            Spacer(modifier = Modifier.padding(16.dp))
            if (currTimeHour > 13) {
                RoundedEdgeCardBody {
                    TekstMedBakgrunn(
                        tekst = "Median pris imorgen",
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
                                getLastDayOfMonth(year.toInt(), (month - 1).toInt()).toString()
                            )
                            median = formatValutaToString(calcMedian(list))
                        }

                        if (isFirstDateOfYear) {

                            val list = fetchResult(
                                (year + 1).toString(),
                                (1).toString(),
                                getLastDayOfMonth((year + 1), 1).toString()
                            )
                            median = formatValutaToString(calcMedian(list))
                        }

                        val list = fetchResult(
                            (year).toString(),
                            (month).toString(),
                            (day + 1).toString()
                        )
                        if (list != null) {
                            median = formatValutaToString(calcMedian(list))
                        }

                        TekstMedBakgrunn(
                            tekst = if (median == "nu") median
                            else if (median.isBlank()) ""
                            else if (!mVa) median
                            else String.format("%.2f", (median.toDouble() * 1.25)),
                            fontSize = pris,
                        )
                        TekstMedBakgrunn(
                            tekst = if (Utils.getValuta() == "NOK") "øre/kWh" else "cent/kWh",
                            modifier = Modifier.padding(top = 32.dp),
                            fontSize = valuta
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(16.dp))
            RoundedEdgeCardBody {
                TekstMedBakgrunn(
                    tekst = "Medianpris for gårsdagen",
                    modifier = Modifier.padding(top = paddingMellomOverskrifter),
                    fontSize = litenOverskrift,
                    fontWeight = FontWeight.Bold
                )
                Row() {
                    if (isFirstDateOfMonth) {
                        val list = fetchResult(
                            year.toString(),
                            (month - 1).toString(),
                            getLastDayOfMonth(year, (month - 1)).toString()
                        )
                        median = formatValutaToString(calcMedian(list))
                    }

                    if (isFirstDateOfYear) {
                        val list = fetchResult(
                            (year - 1).toString(),
                            (1).toString(),
                            getLastDayOfMonth((year - 1), 1).toString()
                        )
                        median = formatValutaToString(calcMedian(list))
                    }


                    val list = fetchResult(
                        (year).toString(),
                        (month).toString(),
                        (day - 1).toString()
                    )
                    if (list != null) {
                        median = formatValutaToString(calcMedian(list))
                    }

                    TekstMedBakgrunn(
                        tekst = if (median == "nu") median
                        else if (median.isBlank()) ""
                        else if (!mVa) median
                        else String.format("%.2f", (median.toDouble() * 1.25)),
                        fontSize = pris
                    )
                    TekstMedBakgrunn(
                        tekst = if (Utils.getValuta() == "NOK") "øre/kWh" else "cent/kWh",
                        modifier = Modifier.padding(top = 32.dp),
                        fontSize = valuta
                    )
                }
            }

            if (Global.valgtSone != "NO4") {
                Row(modifier = Modifier.padding(top = 10.dp)) {
                    mVa = medMvaSwitch(sharedPreferences = sharedPrefMva)
                    TekstMedBakgrunn(
                        tekst = " med mVa",
                        fontSize = datesize,
                        modifier = Modifier.padding(top = 15.dp)
                    )
                }
            } else {
                mVa = false
            }
            Spacer(modifier = Modifier.padding(50.dp))
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

fun formatValutaToString(d: Double?): String {
    return String.format("%.2f",d?.times(100))
}

fun getLastDayOfMonth(year: Int, month: Int): Int {
    val yearMonth =
        YearMonth.of(year, month)
    return yearMonth.lengthOfMonth()
}

fun calcMedian(list : List<PriceData>?): Double {
    if (list != null) {
        var h1 = 0.0
        var h2 = 0.0
        val listSize = (list?.size?.div(2))?.minus(1)
        if (Utils.getValuta() == "NOK") {
            if (list.size % 2 == 1) {
                list.forEachIndexed { index, priceData ->
                    if (index == listSize) {
                        h1 = priceData.nokPerKwh
                    } else if (index == list.size-1) {
                        h2 = priceData.nokPerKwh
                    }
                    return (h1+h2)/2
                }
            } else {
                var value: Double = list.get(list.size - 1).nokPerKwh
                return value
            }
        } else {
            if (list.size % 2 == 1) {
                list.forEachIndexed { index, priceData ->
                    if (index == listSize) {
                        h1 = priceData.eurPerKwh
                    } else if (index == list.size-1) {
                        h2 = priceData.eurPerKwh
                    }
                }
                return (h1+h2)/2
            } else {
                var value: Double = list.get(list.size - 1).eurPerKwh
                return value
            }
        }
    }
    return 0.0
}
