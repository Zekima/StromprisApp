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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter


@SuppressLint("SimpleDateFormat", "CoroutineCreationDuringComposition")
@Preview
@Composable
fun HomeScreen() {
    println("Recompsing")
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val sharedPrefMva = LocalContext.current.getSharedPreferences("mySharedPrefMva", Context.MODE_PRIVATE)
    val litenOverskrift = 18.sp; val pris = 50.sp; val valuta = if(isLandscape)16.sp else 16.sp
    val datesize = 15.sp; val paddingMellomOverskrifter = 10.dp
    var mVa by remember { mutableStateOf(sharedPrefMva.getBoolean("medMva", false))}
    val year =  LocalDate.now().year
    val month = LocalDate.now().monthValue
    val day = LocalDate.now().dayOfMonth
    val list = fetchResult(year = year.toString(), month = month.toString(), day.toString())
    var currTimeHour by remember { mutableIntStateOf(LocalTime.now().hour)}
    var currTimeMinute by remember { mutableIntStateOf(LocalTime.now().minute)}
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
    val isLastDateOfMonth = parsedDate.dayOfMonth == LocalDate.now().lengthOfMonth()
    val isLastDateOfYear = parsedDate.dayOfYear == 365

    DisposableEffect(currTimeHour) {
        val scope = CoroutineScope(Dispatchers.Main)
        val job = scope.launch {
            while (true) {
                println("bob")
                delay(30_000)
                hourHolder = LocalTime.now().hour
                minuteHolder = LocalTime.now().minute
                if (hourHolder>currTimeHour) {
                    println(1)
                    if (minuteHolder > 2) {
                        println(2)
                        currTimeHour = hourHolder
                        currTimeMinute = minuteHolder
                        dagensPrisKr = if (Utils.getValuta() == "NOK") {
                            formatValutaToString(list?.get(currTimeHour)?.nokPerKwh)
                        } else {
                            formatValutaToString(list?.get(currTimeHour)?.eurPerKwh)
                        }
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
                    fontSize = 35.sp,
                    textColor = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))

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
                            tekst = "Strømprisen nå",
                            modifier = Modifier.padding(top = paddingMellomOverskrifter),
                            fontSize = litenOverskrift,
                            fontWeight = FontWeight.Bold
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
                if (currTimeHour >= 13) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        RoundedEdgeCardBodyHorizontal {
                            TekstMedBakgrunn(
                                tekst = "Median imorgen",
                                modifier = Modifier.padding(top = paddingMellomOverskrifter),
                                fontSize = litenOverskrift,
                                fontWeight = FontWeight.Bold
                            )

                            Row {
                                var list: List<PriceData>? = null

                                if (isLastDateOfYear) {
                                    list = fetchResult(
                                        (year+1).toString(),
                                        (1).toString(),
                                        (1).toString()
                                    )
                                } else if (isLastDateOfMonth) {
                                    list = fetchResult(
                                        (year).toString(),
                                        (month+1).toString(),
                                        (1).toString()
                                    )
                                } else {
                                    list = fetchResult(
                                        (year).toString(),
                                        (month).toString(),
                                        (day + 1).toString()
                                    )
                                }
                                median = formatValutaToString(calcMedian(list))

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

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    RoundedEdgeCardBodyHorizontal {
                        TekstMedBakgrunn(
                            tekst = "Median igår",
                            modifier = Modifier.padding(top = paddingMellomOverskrifter),
                            fontSize = litenOverskrift,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            var list: List<PriceData>? = null
                            if (isFirstDateOfMonth) {
                                list = fetchResult(
                                    year.toString(),
                                    (month - 1).toString(),
                                    getLastDayOfMonth(year, (month - 1)).toString()
                                )
                                median = formatValutaToString(calcMedian(list))
                            } else if (isFirstDateOfYear) {
                                list = fetchResult(
                                    (year - 1).toString(),
                                    (12).toString(),
                                    getLastDayOfMonth((year - 1), 12).toString()
                                )
                                median = formatValutaToString(calcMedian(list))
                            } else {
                                list = fetchResult(
                                    (year).toString(),
                                    (month).toString(),
                                    (day - 1).toString()
                                )
                            }
                            median = formatValutaToString(calcMedian(list))


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


            }

            if (Global.valgtSone != "NO4") {
                Spacer(modifier = Modifier.padding(8.dp))
                Row {
                    mVa = medMvaSwitch(sharedPreferences = sharedPrefMva)
                    TekstMedBakgrunn(
                        tekst = " med mVa",
                        fontSize = datesize,
                        modifier = Modifier.padding(top = 15.dp),
                        textColor = MaterialTheme.colorScheme.onBackground
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
                    fontSize = 35.sp,
                    textColor = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))
            RoundedEdgeCardBody {
                TekstMedBakgrunn(
                    tekst = "Strømprisen nå",
                    modifier = Modifier.padding(top = paddingMellomOverskrifter),
                    fontSize = litenOverskrift,
                    fontWeight = FontWeight.Bold
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

            Spacer(modifier = Modifier.padding(8.dp))
            if (currTimeHour >= 13) {
                RoundedEdgeCardBody {
                    TekstMedBakgrunn(
                        tekst = "Median pris imorgen",
                        modifier = Modifier.padding(top = paddingMellomOverskrifter),
                        fontSize = litenOverskrift,
                        fontWeight = FontWeight.Bold
                    )

                    Row() {
                        var list: List<PriceData>? = null
                        if (isLastDateOfYear) {
                            list = fetchResult(
                                (year+1).toString(),
                                (1).toString(),
                                (1).toString()
                            )
                            median = formatValutaToString(calcMedian(list))
                        } else if (isLastDateOfMonth) {
                            list = fetchResult(
                                (year).toString(),
                                (month+1).toString(),
                                (1).toString()
                            )
                        } else {
                            list = fetchResult(
                                (year).toString(),
                                (month).toString(),
                                (day + 1).toString()
                            )
                        }
                        median = formatValutaToString(calcMedian(list))

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
            Spacer(modifier = Modifier.padding(8.dp))
            RoundedEdgeCardBody {
                TekstMedBakgrunn(
                    tekst = "Medianpris for gårsdagen",
                    modifier = Modifier.padding(top = paddingMellomOverskrifter),
                    fontSize = litenOverskrift,
                    fontWeight = FontWeight.Bold
                )
                Row() {
                    var list: List<PriceData>? = null
                    if (isFirstDateOfMonth) {
                        list = fetchResult(
                            year.toString(),
                            (month - 1).toString(),
                            getLastDayOfMonth(year, (month - 1)).toString()
                        )
                    } else if (isFirstDateOfYear) {
                        list = fetchResult(
                            (year - 1).toString(),
                            (1).toString(),
                            getLastDayOfMonth((year - 1), 1).toString()
                        )
                    } else {
                        list = fetchResult(
                            (year).toString(),
                            (month).toString(),
                            (day - 1).toString()
                        )
                    }
                    median = formatValutaToString(calcMedian(list))

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
                Row(modifier = Modifier.padding(top = 18.dp)) {
                    mVa = medMvaSwitch(sharedPreferences = sharedPrefMva)
                    TekstMedBakgrunn(
                        tekst = " med mVa",
                        fontSize = datesize,
                        modifier = Modifier.padding(top = 15.dp),
                        textColor = MaterialTheme.colorScheme.onBackground
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
    return String.format("%.1f",d?.times(100))
}

fun getLastDayOfMonth(year: Int, month: Int): Int {
    val yearMonth =
        YearMonth.of(year, month)
    return yearMonth.lengthOfMonth()
}
fun calcMedian(list: List<PriceData>?): Double {
    if (list == null || list.isEmpty()) return 0.0
    val even = list.size % 2 == 0
    val middle = (list.size -1) / 2
    val medianValue: Double = if (Utils.getValuta() == "NOK") {
        list.sortedBy { it.nokPerKwh }.let {
            if (even) (it[middle - 1].nokPerKwh + it[middle].nokPerKwh) / 2 else it[middle].nokPerKwh
        }
    } else {
        list.sortedBy { it.eurPerKwh }.let {
            if (even) (it[middle - 1].eurPerKwh + it[middle].eurPerKwh) / 2 else it[middle].eurPerKwh
        }
    }
    return medianValue
}

@Preview
@Composable
fun HomePrev() {
    HomeScreen()
}
