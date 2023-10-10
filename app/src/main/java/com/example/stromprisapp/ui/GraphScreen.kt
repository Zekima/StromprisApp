package com.example.stromprisapp.ui

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
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stromprisapp.PriceData
import com.example.stromprisapp.Utils.convertTime
import com.example.stromprisapp.Utils.fetchApiData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

    Column(modifier = Modifier.padding(top = 100.dp, start = 16.dp, end = 16.dp)) {
        Row(modifier = Modifier.padding(bottom = 5.dp)) {
            Text(text = "Region: Oslo / NO1")
            Spacer(modifier = Modifier.width(5.dp))
            Box(
                Modifier.clickable { navController.navigate("settings") }
            ) {
                Text(
                    text = "Endre",
                    color = Color(android.graphics.Color.parseColor("#a356f0"))
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    selectedDate = currentDate
                    activeButton = "today"
                    selectedDataPoint = null
                },
                shape = RectangleShape,
                border = BorderStroke(1.dp, Color.Black),
                colors = ButtonDefaults.buttonColors(
                    if (activeButton == "today") Color.Black else Color.Gray
                )
            ) {
                Text("I dag")
            }
            Button(
                onClick = {
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                    selectedDate = dateFormat.format(calendar.time)
                    activeButton = "tomorrow"
                    selectedDataPoint = null
                },
                shape = RectangleShape,
                border = BorderStroke(1.dp, Color.Black),
                colors = ButtonDefaults.buttonColors(
                    if (activeButton == "today") Color.Gray else Color.Black
                )
            ) {
                Text("I morgen")
            }
        }

        val (year, month, day) = selectedDate.split("-")

        val fetchResult =
            produceState<List<PriceData>?>(initialValue = null, key1 = selectedDate) {
                value = withContext(Dispatchers.IO) {
                    fetchApiData(year, month, day, "NO1")
                }
                dataChanged = true
            }


        var sortedData = fetchResult.value?.sortedBy { convertTime(it.timeStart, "HH") }
        var xScale = 0f
        var yScale = 0f
        var maxPrice = 0f
        var minPrice = 0f

        val hitboxRecs = remember { mutableStateOf(ArrayList<Rect>()) }
        val pointRecs = remember { mutableStateOf(ArrayList<Rect>()) }

        var checked by remember { mutableStateOf(true) }

        Box(
            modifier = Modifier
                .aspectRatio(3 / 2f)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { tapOffset ->
                            var sortedData = fetchResult.value?.sortedBy { convertTime(it.timeStart, "HH") }
                            hitboxRecs.value.forEachIndexed { index, rect ->
                                if (rect.contains(tapOffset)) {
                                    selectedDataPoint = sortedData?.get(index)
                                    selectedDataPointIndex = index
                                }
                            }
                        }
                    )
                }
                .drawWithCache {
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
                        val barWidthPx = 1.dp.toPx();
                        drawRect(Color.Gray, style = Stroke(barWidthPx))

                        val lines = 4;
                        val verticalSize = size.width / (lines + 1)
                        val sectionSize = size.height / (lines + 1)
                        repeat(lines) { i ->
                            val startX = verticalSize * (i + 1)
                            val startY = sectionSize * (i + 1)
                            drawLine(
                                Color.Gray,
                                start = Offset(startX, 0f),
                                end = Offset(startX, size.height),
                                strokeWidth = barWidthPx
                            )
                            drawLine(
                                Color.Gray,
                                start = Offset(0f, startY),
                                end = Offset(size.width, startY),
                                strokeWidth = barWidthPx
                            )
                        }

                        if (dataChanged) {
                            if (path != null) {
                                drawPath(path, Color.Gray, style = Stroke(4.dp.toPx()))
                            }

                            val paint = android.graphics
                                .Paint()
                                .apply {
                                    color = android.graphics.Color.BLACK
                                    textSize = 28f
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }

                            if (sortedData != null) {
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
                                        (size.height - (priceData.nokPerKwh - minPrice) * yScale).toFloat()

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
                }
        ) {

            if (activeButton === "today" && sortedData !== null && selectedDataPoint === null) {
                if ((selectedHour) < sortedData.size) {
                    selectedDataPoint = sortedData[selectedHour]
                    selectedDataPointIndex = selectedHour
                } else {
                    selectedDataPoint = null
                }
            }

            if (selectedDataPoint !== null && selectedDataPointIndex >= 0 && selectedDataPointIndex < pointRecs.value.size) {
                var currentRect = pointRecs.value.get(selectedDataPointIndex)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer(
                                translationX = currentRect.left - 69,
                                translationY = currentRect.top - 140
                            )
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black)
                            .width(60.dp)
                            .height(45.dp),
                        contentAlignment = Alignment.Center

                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Kl: " + convertTime(selectedDataPoint!!.timeStart, "HH"),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                text = "${"%.2f".format(selectedDataPoint!!.nokPerKwh * 100)} øre"
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .graphicsLayer(
                                translationX = currentRect.left - 69,
                                translationY = currentRect.top - 140
                            )
                            .width(30.dp)
                            .height(15.dp)
                            .rotate(180f)
                            .background(Color.Black, shape = TriangleShape)
                    )

                }


            }
        }

        Row(modifier = Modifier.padding(top = 15.dp), verticalAlignment = Alignment.CenterVertically) {


            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                }
            )

            Text(text = "Inkluder nettleie, avgifter og mva", modifier = Modifier.padding(start = 5.dp))
        }
    }

}

private fun generatePath(data: List<PriceData>, xScale: Float, yScale: Float, size: Size): Path {
    val path = Path()
    val minPrice = data.minOf { it.nokPerKwh }

    data.forEachIndexed { i, priceData ->
        val x = i * xScale
        val y = (size.height - (priceData.nokPerKwh - minPrice) * yScale).toFloat()

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }

    return path
}


//https://foso.github.io/Jetpack-Compose-Playground/cookbook/how_to_create_custom_shape/
private val TriangleShape = GenericShape { size, _ ->
    moveTo(size.width / 2f, 0f)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
}
