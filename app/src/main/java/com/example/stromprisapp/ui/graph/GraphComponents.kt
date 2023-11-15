package com.example.stromprisapp.ui.graph

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stromprisapp.PriceData
import com.example.stromprisapp.Utils
import com.example.stromprisapp.Utils.convertZoneCode
import com.example.stromprisapp.Utils.fetchApiData
import com.example.stromprisapp.Utils.getValuta
import com.example.stromprisapp.Utils.includeFees
import com.example.stromprisapp.ui.Global.valgtSone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Composable funskjon som skal vise regionen og en knapp for å endre dette
 *
 * @param navController For å navigere gjennom de ulike composables.
 */
@Composable
fun RegionRow(navController: NavController) {
    Row(modifier = Modifier.padding(top = 10.dp)) {
        val currentRegion = convertZoneCode(valgtSone)
        Text(text = "Region: $currentRegion", color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.width(5.dp))
        Box(
            Modifier.clickable { navController.navigate("settings") }
        ) {
            Text(
                text = "Endre",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Composable funkjson som skal vise hvilken dager knappene.
 *
 * @param activeButton String for den valgte aktive knappen.
 * @param onSelectYesterday Callback når "Yesterday" knappen er selektert.
 * @param onSelectToday Callback når "Today" knappen er selektert.
 * @param onSelectTomorrow Callback når "Tomorrow" knappen er selektert.
 */
@Composable
fun DateSelector(
    activeButton: String,
    onSelectYesterday: () -> Unit,
    onSelectToday: () -> Unit,
    onSelectTomorrow: () -> Unit
) {
    Row {
        DateButton("I går", activeButton == "yesterday", onSelectYesterday)
        DateButton("I dag", activeButton == "today", onSelectToday)
        DateButton("I morgen", activeButton == "tomorrow", onSelectTomorrow)
    }
}
/**
 * Composable funkjson som representerer en knapp for date.
 *
 * @param text Teksten som skal vises på knappen.
 * @param isActive Om knappen er "Aktiv" eller ikke.
 * @param onClick Callback når knappen er trykket.
 */
@Composable
fun DateButton(text: String, isActive: Boolean, onClick: () -> Unit) {
    Button(
        onClick = {
            if (!isActive) onClick()
        },
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        )
    ) {
        Text(text)
    }
}

/**
 * Composable funkjson som viser en tooltip for prisen for ett bestemt klokkelsett når man klikker på grafen.
 *
 * @param selectedDataPoint PriceData som representerer det bestemte punket på grafen.
 * @param rect Rektangelet som vises når man klikker på grafen.
 * @param includeFees Om ekstra kostander skal vises.
 */
@Composable
fun PriceTooltip(
    selectedDataPoint: PriceData?,
    rect: Rect,
    includeFees: Boolean
) {
    if (selectedDataPoint != null) {

        // eur/nok revamp trengs

        val pricePerKwhValue = if (getValuta() == "NOK") {
            selectedDataPoint.nokPerKwh * 100
        } else {
            selectedDataPoint.eurPerKwh * 100
        }

        val displayPrice = if (includeFees) {
            val feesIncludedValue = includeFees(pricePerKwhValue)
            if (feesIncludedValue >= 100) String.format("%.0f", feesIncludedValue)
            else String.format("%.2f", feesIncludedValue)
        } else {
            if (pricePerKwhValue >= 100) String.format("%.0f", pricePerKwhValue)
            else String.format("%.2f", pricePerKwhValue)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .graphicsLayer(
                        translationX = rect.left - 75,
                        translationY = rect.top - 195
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
                    .width(65.dp)
                    .height(65.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Kl: " + Utils.convertTime(selectedDataPoint.timeStart, "HH"),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        text = displayPrice
                    )
                    Text(
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        text = if (getValuta() == "NOK") "øre" else "cent"
                    )
                }
            }
            Box(
                modifier = Modifier
                    .graphicsLayer(
                        translationX = rect.left - 75,
                        translationY = rect.top - 195
                    )
                    .width(30.dp)
                    .height(15.dp)
                    .rotate(180f)
                    .background(Color.Black, shape = TriangleShape)
            )
        }
    }
}
/**
 * Composable funksjon som fetcher prisen for en dato og sone fra API.
 *
 * @param selectedDate Dato med formatet: "yyyy-MM-dd" format.
 * @param onResult Callback når man fetched data eller null hvis noe gikk dårlig.
 */
@Composable
fun FetchPriceData(selectedDate: String, onResult: (List<PriceData>?) -> Unit) {
    val fetchResult = remember { mutableStateOf<List<PriceData>?>(null) }

    LaunchedEffect(selectedDate) {
        try {
            val (year, month, day) = selectedDate.split("-")
            val result = withContext(Dispatchers.IO) {
                fetchApiData(year, month, day, valgtSone)
            }
            fetchResult.value = result
        } finally {
            onResult(fetchResult.value)
        }
    }
}
/**
 * Composable funksjon som viser prisgrafen, grid, path, labels og data points.
 *
 * @param sortedData PriceData objektene som representerer alle punktene for grafen.
 * @param selectedDataPoint Selektert PriceData punkt.
 * @param onSelectedDataPointChanged Callback punkene endres.
 * @param selectedDataPointIndex Index på selektert punkt.
 * @param onSelectedDataPointIndexChanged Callback når index på selektert punkt endres.
 * @param isLoading Om data blir lastet.
 * @param activeButton Valgt knapp (i dag, i går, i morgen).
 * @param checked Om MvA skal inkluderes.
 */
@Composable
fun GraphContent(
    sortedData: List<PriceData>?,
    selectedDataPoint: PriceData?,
    onSelectedDataPointChanged: (PriceData?) -> Unit,
    selectedDataPointIndex: Int,
    onSelectedDataPointIndexChanged: (Int) -> Unit,
    isLoading: Boolean,
    activeButton: String,
    checked: Boolean,
) {
    val hitboxRecs = remember { mutableStateOf(ArrayList<Rect>()) }
    val pointRecs = remember { mutableStateOf(ArrayList<Rect>()) }

    val hourFormat = SimpleDateFormat("HH", Locale.getDefault())
    val currentHour = remember { hourFormat.format(Date()).toInt() }
    var selectedHour by remember { mutableIntStateOf(currentHour) }

    var path: Path? = null

    val boxSize = remember { mutableStateOf(Size(0f, 0f)) }

    var showTooltip by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE


    var xScale = 0f
    var yScale = 0f
    var minPrice = 0f
    var maxPrice: Float

    showTooltip = false;

    val paint = android.graphics
        .Paint()
        .apply {
            color = MaterialTheme.colorScheme.onBackground.toArgb()
            textSize = 29f
            textAlign = android.graphics.Paint.Align.RIGHT
        }

    Box(
        modifier = Modifier
            .aspectRatio(if (isLandscape) 9 / 3f else 3 / 2f)
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { tapOffset ->
                        hitboxRecs.value.forEachIndexed { index, rect ->
                            if (rect.contains(tapOffset) && sortedData != null) {
                                onSelectedDataPointChanged(sortedData[index])
                                onSelectedDataPointIndexChanged(index)
                            }
                        }
                    }
                )
            }

            .drawWithCache {

                boxSize.value = size
                var yAxisLabelCount = 0;
                var yAxisInterval = 0f;

                // eur/nok revamp

                if (sortedData != null) {
                    maxPrice = sortedData
                        .maxOf { it.nokPerKwh }
                        .toFloat()
                    minPrice = sortedData
                        .minOf { it.nokPerKwh }
                        .toFloat()

                    val paddingPercentage = 0.25f
                    val availableHeight = (1 - paddingPercentage) * boxSize.value.height

                    yScale = availableHeight / (maxPrice - minPrice)
                    xScale = boxSize.value.width / (sortedData.size - 1)

                    path = generatePath(sortedData, xScale, yScale, boxSize.value)

                    yAxisLabelCount = 4
                    yAxisInterval = (maxPrice - minPrice) / (yAxisLabelCount - 1)
                }

                onDrawBehind {
                    drawGrid(boxSize.value)
                    if (path != null) {
                        drawPath(path!!, Color.Gray, style = Stroke(4.dp.toPx()))
                    }

                    for (i in 0 until yAxisLabelCount) {

                        // eur/nok revamp

                        val priceLabelValue = if (checked) {
                            includeFees(
                                (minPrice + i * yAxisInterval).toDouble() * 100
                            ).toFloat()
                        } else {
                            (minPrice + i * yAxisInterval) * 100
                        }

                        val priceLabelValueEur = priceLabelValue / 11.8
                        val priceLabel = if (getValuta() == "NOK") {
                            String.format("%.0f", priceLabelValue)
                        } else {
                            String.format("%.0f", priceLabelValueEur)
                        }

                        val y = boxSize.value.height - ((minPrice + i * yAxisInterval) - minPrice) * yScale - 50f

                        drawContext.canvas.nativeCanvas.drawText(
                            priceLabel,
                            0f,
                            y,
                            paint
                        )
                    }

                    sortedData?.forEachIndexed { i, priceData ->
                        val x = i * xScale
                        val y = boxSize.value.height + 40

                        val timeLabel = Utils.convertTime(priceData.timeStart, "HH")

                        drawContext.canvas.nativeCanvas.drawText(
                            timeLabel,
                            x,
                            y,
                            paint
                        )
                    }

                    hitboxRecs.value.clear()
                    pointRecs.value.clear()

                    val circleRadius = 4.dp.toPx()

                    val sectionWidth = boxSize.value.width / sortedData!!.size

                    sortedData?.forEachIndexed { i, priceData ->
                        val x = i * xScale
                        val y =
                            (boxSize.value.height - (priceData.nokPerKwh - minPrice) * yScale - bottomOffset).toFloat()

                        drawCircle(
                            Color.DarkGray,
                            radius = circleRadius,
                            center = Offset(x, y)
                        )

                        val pointRect = Rect(
                            top = y - circleRadius,
                            left = x - circleRadius,
                            bottom = y + circleRadius,
                            right = x + circleRadius
                        )

                        pointRecs.value.add(pointRect)

                        // could be reworked
                        val hitboxLeft = i * sectionWidth
                        val hitboxRight = hitboxLeft + sectionWidth

                        val hitboxRect = Rect(
                            top = 0f,
                            left = hitboxLeft,
                            bottom = boxSize.value.height,
                            right = hitboxRight
                        )

                        hitboxRecs.value.add(hitboxRect)
                    }
                }
            }
        )
        {

        LaunchedEffect(sortedData) {
            showTooltip = true
        }

        if (showTooltip && activeButton === "today" && sortedData !== null && selectedDataPoint === null) {
            if ((selectedHour) < sortedData.size) {
                onSelectedDataPointChanged(sortedData[selectedHour])
                onSelectedDataPointIndexChanged(selectedHour)
            } else {
                onSelectedDataPointChanged(null)
            }
        }

        if (!isLoading && selectedDataPoint !== null && selectedDataPointIndex < pointRecs.value.size) {
            val currentRect = pointRecs.value[selectedDataPointIndex]
            PriceTooltip(
                selectedDataPoint = selectedDataPoint,
                rect = currentRect,
                includeFees = checked
            )
        }
    }
}


