package com.example.stromprisapp.ui.graph

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
   EUR/NOK tilpasning (trenger ikke å skifte selve path/datapoins siden det er relativ)
   Automatisk tooltip for idag og klokkeslett
   Darkmode/light mode tilpasning


*/

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


    var selectedDate by remember { mutableStateOf(currentDate) }

    var activeButton by remember { mutableStateOf("today") }

    var isLoading by remember { mutableStateOf(false) }

    var shouldUpdateDataPoint by remember { mutableStateOf(true) }
    var showTooltip by remember { mutableStateOf(false) }

    var sortedData by remember { mutableStateOf<List<PriceData>?>(null) }

    Column(modifier = Modifier.padding(top = 100.dp, start = 16.dp, end = 16.dp)) {

        RegionRow(navController = navController)

        DateSelector(
            activeButton = activeButton,
            onSelectToday = {
                if (activeButton == "today") return@DateSelector
                selectedDataPoint = null
                selectedDate = currentDate
                activeButton = "today"
                shouldUpdateDataPoint = true
            },
            onSelectTomorrow = {
                if (activeButton == "tomorrow") return@DateSelector
                selectedDataPoint = null
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                selectedDate = dateFormat.format(calendar.time)
                activeButton = "tomorrow"
                shouldUpdateDataPoint = true
            }
        )

        var checked by remember { mutableStateOf(true) }

        FetchPriceData(selectedDate) { result ->
            if (result != null) {
                sortedData = result.sortedBy { convertTime(it.timeStart, "HH") }
            } else {
                sortedData = null
            }
        }

        if (sortedData == null) {
            Text(text = "Strømpriser for i morgen publiseres kl 13:00 i dag")
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

