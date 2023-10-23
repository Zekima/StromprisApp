package com.example.stromprisapp.ui.graph

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stromprisapp.PriceData
import com.example.stromprisapp.Utils.convertTime
import com.example.stromprisapp.Utils.fetchApiData
import com.example.stromprisapp.ui.Global.valgtSone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

const val bottomOffset = 50f;

@Composable
fun GraphScreen(navController: NavController) {

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val currentDate = remember { dateFormat.format(Date()) }

    val hourFormat = SimpleDateFormat("HH", Locale.getDefault())
    val currentHour = remember { hourFormat.format(Date()).toInt() }

    var selectedHour by remember { mutableIntStateOf(currentHour) }

    var selectedDataPoint by remember { mutableStateOf<PriceData?>(null) }
    var selectedDataPointIndex by remember { mutableIntStateOf(0) }
    var boxSize by remember { mutableStateOf(Size(0f, 0f)) }

    var scaleState by remember { mutableStateOf(Pair(0f, 0f)) }

    var selectedDate by remember { mutableStateOf(currentDate) }
    var dataChanged by remember { mutableStateOf(false) }

    var activeButton by remember { mutableStateOf("today") }

    var isLoading by remember { mutableStateOf(false) }

    var shouldUpdateDataPoint by remember { mutableStateOf(true) }
    var showTooltip by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(top = 100.dp, start = 16.dp, end = 16.dp)) {

        RegionRow(navController = navController)

        DateSelector(
            activeButton = activeButton,
            onSelectToday = {
                selectedDataPoint = null
                selectedDate = currentDate
                activeButton = "today"
                dataChanged = false
                shouldUpdateDataPoint = true
            },
            onSelectTomorrow = {
                selectedDataPoint = null
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                selectedDate = dateFormat.format(calendar.time)
                activeButton = "tomorrow"
                dataChanged = false
                shouldUpdateDataPoint = true
            }
        )

        val fetchResult = remember { mutableStateOf<List<PriceData>?>(null) }

        LaunchedEffect(selectedDate) {
            isLoading = true
            try {
                val (year, month, day) = selectedDate.split("-")
                val result = withContext(Dispatchers.IO) {
                    fetchApiData(year, month, day, valgtSone)
                }
                fetchResult.value = result
                dataChanged = true
            } finally {
                isLoading = false
            }
        }

        LaunchedEffect(fetchResult.value) {
            if (fetchResult.value != null) {
                isLoading = false
            }
        }

        var xScale = 0f
        var yScale = 0f
        var minPrice = 0f
        var maxPrice: Float

        val hitboxRecs = remember { mutableStateOf(ArrayList<Rect>()) }
        val pointRecs = remember { mutableStateOf(ArrayList<Rect>()) }
        var checked by remember { mutableStateOf(true) }

        // Sorter kun når fetchResult endrer seg
        val sortedData = remember(fetchResult.value) {
            fetchResult.value?.sortedBy { convertTime(it.timeStart, "HH") }
        }

        if (sortedData == null) {
            return;
        }

        Box(
            modifier = Modifier
                .aspectRatio(3 / 2f)
                .fillMaxSize()
                .pointerInput(Unit) {
                    val sortedDataLocal = sortedData
                    detectTapGestures(
                        onTap = { tapOffset ->
                            hitboxRecs.value.forEachIndexed { index, rect ->
                                if (rect.contains(tapOffset) && sortedDataLocal != null) {
                                    selectedDataPoint = sortedDataLocal[index]
                                    selectedDataPointIndex = index
                                }
                            }
                        }
                    )
                }
                .drawWithCache {

                    Log.d("GraphScreen", "drawWithCache recomposed")

                    var path: Path? = null
                    boxSize = size

                    if (dataChanged && sortedData != null) {
                        maxPrice = sortedData
                            .maxOf { it.nokPerKwh }
                            .toFloat()
                        minPrice = sortedData
                            .minOf { it.nokPerKwh }
                            .toFloat()

                        val paddingPercentage = 0.25f
                        val availableHeight = (1 - paddingPercentage) * size.height

                        yScale = availableHeight / (maxPrice - minPrice)
                        xScale = size.width / (sortedData.size - 1)

                        scaleState = Pair(xScale, yScale)

                        path = generatePath(sortedData, xScale, yScale, size)
                    }

                    onDrawBehind {
                        drawGrid(size)

                        if (dataChanged) {
                            if (path != null) {
                                drawPath(path, Color.Gray, style = Stroke(4.dp.toPx()))
                                println("New path drawn?")
                            }

                            val paint = android.graphics
                                .Paint()
                                .apply {
                                    color = android.graphics.Color.BLACK
                                    textSize = 28f
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }

                            sortedData.forEachIndexed { i, priceData ->
                                val x = i * xScale
                                val y = size.height + 40

                                val timeLabel = convertTime(priceData.timeStart, "HH")

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
                            sortedData.forEachIndexed { i, priceData ->
                                val x = i * xScale
                                val y =
                                    (size.height - (priceData.nokPerKwh - minPrice) * yScale - bottomOffset).toFloat()

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
                                    bottom = size.height,
                                    right = x + (circleRadius + 9)
                                )

                                hitboxRecs.value.add(hitboxRect)
                            }
                        }
                    }
                }
        ) {
            if (shouldUpdateDataPoint && activeButton === "today" && sortedData !== null && selectedDataPoint === null) {
                if ((selectedHour) < sortedData.size) {
                    selectedDataPoint = sortedData[selectedHour]
                    selectedDataPointIndex = selectedHour
                } else {
                    selectedDataPoint = null
                }
                shouldUpdateDataPoint = false
            }

            showTooltip =
                !isLoading && selectedDataPoint !== null && selectedDataPointIndex < pointRecs.value.size

            if (showTooltip) {
                val currentRect = pointRecs.value[selectedDataPointIndex]
                PriceTooltip(
                    selectedDataPoint = selectedDataPoint,
                    rect = currentRect,
                    includeFees = checked
                )
            }
        }
        Row(
            modifier = Modifier.padding(top = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                }
            )
            Text(
                text = "Inkluder nettleie, avgifter og mva",
                modifier = Modifier.padding(start = 5.dp)
            )
        }
    }

}

private fun generatePath(data: List<PriceData>, xScale: Float, yScale: Float, size: Size): Path {

    val path = Path()
    val minPrice = data.minOf { it.nokPerKwh }

    data.forEachIndexed { i, priceData ->
        val x = i * xScale
        val y = (size.height - (priceData.nokPerKwh - minPrice) * yScale - bottomOffset).toFloat()

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }

    return path
}

fun DrawScope.drawGrid(size: Size, lines: Int = 4) {
    val barWidthPx = 1.dp.toPx()
    val color = Color.Gray

    val verticalSize = size.width / (lines + 1)
    val sectionSize = size.height / (lines + 1)

    drawRect(color, style = Stroke(barWidthPx))

    repeat(lines) { i ->
        val startX = verticalSize * (i + 1)
        val startY = sectionSize * (i + 1)
        drawLine(
            color,
            start = Offset(startX, 0f),
            end = Offset(startX, size.height),
            strokeWidth = barWidthPx
        )
        drawLine(
            color,
            start = Offset(0f, startY),
            end = Offset(size.width, startY),
            strokeWidth = barWidthPx
        )
    }
}