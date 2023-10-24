package com.example.stromprisapp.ui.graph

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stromprisapp.PriceData
import com.example.stromprisapp.Utils
import com.example.stromprisapp.Utils.fetchApiData
import com.example.stromprisapp.Utils.includeFees
import com.example.stromprisapp.ui.Global.valgtSone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun RegionRow(navController: NavController) {
    Row(modifier = Modifier.padding(bottom = 5.dp)) {
        Text(text = "Region: Oslo / NO1")
        Spacer(modifier = Modifier.width(5.dp))
        Box(
            Modifier.clickable { navController.navigate("settings") }
        ) {
            Text(
                text = "Endre",
                color = androidx.compose.ui.graphics.Color.Magenta
            )
        }
    }
}

@Composable
fun DateSelector(
    activeButton: String,
    onSelectToday: () -> Unit,
    onSelectTomorrow: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = onSelectToday,
            shape = RectangleShape,
            border = BorderStroke(1.dp, androidx.compose.ui.graphics.Color.Black),
            colors = ButtonDefaults.buttonColors(
                if (activeButton == "today") androidx.compose.ui.graphics.Color.Black else androidx.compose.ui.graphics.Color.Gray
            )
        ) {
            Text("I dag")
        }
        Button(
            onClick = onSelectTomorrow,
            shape = RectangleShape,
            border = BorderStroke(1.dp, androidx.compose.ui.graphics.Color.Black),
            colors = ButtonDefaults.buttonColors(
                if (activeButton == "today") androidx.compose.ui.graphics.Color.Gray else androidx.compose.ui.graphics.Color.Black
            )
        ) {
            Text("I morgen")
        }
    }
}


@Composable
fun PriceTooltip(
    selectedDataPoint: PriceData?,
    rect: Rect,
    includeFees: Boolean
) {
    if (selectedDataPoint != null) {

        // eur/nok revamp trengs

        val orePerKwhValue = selectedDataPoint.nokPerKwh * 100
        val displayPrice = if (includeFees) {
            val feesIncludedValue = Utils.includeFees(orePerKwhValue, "NOK")
            if (feesIncludedValue >= 100) String.format("%.0f", feesIncludedValue)
            else String.format("%.2f", feesIncludedValue)
        } else {
            if (orePerKwhValue >= 100) String.format("%.0f", orePerKwhValue)
            else String.format("%.2f", orePerKwhValue)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .graphicsLayer(
                        translationX = rect.left - 69,
                        translationY = rect.top - 140
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .background(androidx.compose.ui.graphics.Color.Black)
                    .width(60.dp)
                    .height(45.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Kl: " + Utils.convertTime(selectedDataPoint.timeStart, "HH"),
                        color = androidx.compose.ui.graphics.Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        textAlign = TextAlign.Center,
                        color = androidx.compose.ui.graphics.Color.White,
                        text = "$displayPrice øre"
                    )
                }
            }
            Box(
                modifier = Modifier
                    .graphicsLayer(
                        translationX = rect.left - 69,
                        translationY = rect.top - 140
                    )
                    .width(30.dp)
                    .height(15.dp)
                    .rotate(180f)
                    .background(androidx.compose.ui.graphics.Color.Black, shape = TriangleShape)
            )
        }
    }
}

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

@Composable
fun GraphContent(
    sortedData: List<PriceData>?,
    selectedDataPoint: PriceData?,
    onSelectedDataPointChanged: (PriceData?) -> Unit,
    selectedDataPointIndex: Int,
    onSelectedDataPointIndexChanged: (Int) -> Unit,
    isLoading: Boolean,
    checked: Boolean
) {
    val hitboxRecs = remember { mutableStateOf(ArrayList<Rect>()) }
    val pointRecs = remember { mutableStateOf(ArrayList<Rect>()) }

    var path: Path? = null

    val boxSize = remember { mutableStateOf(Size(0f, 0f)) }

    var xScale = 0f
    var yScale = 0f
    var minPrice = 0f
    var maxPrice: Float

    Box(
        modifier = Modifier
            .aspectRatio(3 / 2f)
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

                    val paint = android.graphics
                        .Paint()
                        .apply {
                            color = android.graphics.Color.BLACK
                            textSize = 29f
                            textAlign = android.graphics.Paint.Align.RIGHT
                        }

                    for (i in 0 until yAxisLabelCount) {

                        // eur/nok revamp

                        val priceLabelValue = if (checked) {
                            includeFees(
                                (minPrice + i * yAxisInterval).toDouble() * 100,
                                "NOK"
                            ).toFloat()
                        } else {
                            (minPrice + i * yAxisInterval) * 100
                        }

                        val priceLabel = String.format("%.0f", priceLabelValue)

                        val y =
                            boxSize.value.height - ((minPrice + i * yAxisInterval) - minPrice) * yScale - 50f

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

                        val hitboxRect = Rect(
                            top = 0f,
                            left = x - (circleRadius + 9),
                            bottom = boxSize.value.height,
                            right = x + (circleRadius + 9)
                        )

                        hitboxRecs.value.add(hitboxRect)
                    }
                }
            }
    ) {

        // legg til auto tooltip popup for idag

//        if (shouldUpdateDataPoint && activeButton === "today" && sortedData !== null && selectedDataPoint === null) {
//            if ((selectedHour) < sortedData.size) {
//                selectedDataPoint = sortedData[selectedHour]
//                selectedDataPointIndex = selectedHour
//            } else {
//                selectedDataPoint = null
//            }
//            shouldUpdateDataPoint = false
//        }

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


