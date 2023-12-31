package com.example.stromprisapp.ui.graph

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stromprisapp.PriceData
import com.example.stromprisapp.Utils.convertTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

const val bottomOffset = 50f;

/*
todo:
   Automatisk tooltip for idag og klokkeslett
   Darkmode/light mode tilpasning
*/

@Composable
fun GraphScreen(navController: NavController) {

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val currentDate = remember {dateFormat.format(Date())}

    var selectedDataPoint by remember { mutableStateOf<PriceData?>(null) }
    var selectedDataPointIndex by remember { mutableIntStateOf(0) }

    var selectedDate by remember { mutableStateOf(currentDate) }

    var activeButton by remember { mutableStateOf("today") }

    var isLoading by remember { mutableStateOf(false) }

    var sortedData by remember { mutableStateOf<List<PriceData>?>(null) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)

    ) {
        if (!isLandscape) RegionRow(navController = navController)
        Row () {
            DateSelector(
                activeButton = activeButton,
                onSelectYesterday = {
                    if (activeButton != "yesterday") {
                        selectedDataPoint = null
                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.DAY_OF_YEAR, -1)
                        selectedDate = dateFormat.format(calendar.time)
                        activeButton = "yesterday"
                    }
                },
                onSelectToday = {
                    if (activeButton != "today") {
                        selectedDataPoint = null
                        selectedDate = dateFormat.format(Calendar.getInstance().time)
                        activeButton = "today"
                    }
                },
                onSelectTomorrow = {
                    if (activeButton != "tomorrow") {
                        selectedDataPoint = null
                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.DAY_OF_YEAR, 1)
                        selectedDate = dateFormat.format(calendar.time)
                        activeButton = "tomorrow"
                    }
                }
            )
            Spacer(modifier = Modifier.width(5.dp))
            if (isLandscape) RegionRow(navController = navController)
        }
        var checked by remember { mutableStateOf(true) }
        FetchPriceData(selectedDate) { result ->
            if (result != null) {
                sortedData = result.sortedBy { convertTime(it.timeStart, "HH") }
            } else {
                sortedData = null
            }
        }

        if (sortedData == null) {
            Text(text = "Strømpriser for i morgen publiseres kl 13:00 i dag", modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onBackground)
            return
        }


        GraphContent(
            sortedData = sortedData,
            selectedDataPoint = selectedDataPoint,
            onSelectedDataPointChanged = { newDataPoint ->
                selectedDataPoint = newDataPoint
            },
            selectedDataPointIndex = selectedDataPointIndex,
            onSelectedDataPointIndexChanged = { newIndex ->
                selectedDataPointIndex = newIndex
            },
            isLoading = isLoading,
            activeButton = activeButton,
            checked = checked
        )

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
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 5.dp)
            )
        }


    }
}


fun generatePath(data: List<PriceData>, xScale: Float, yScale: Float, size: Size): Path {

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

