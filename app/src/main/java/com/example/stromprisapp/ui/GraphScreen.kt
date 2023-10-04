package com.example.stromprisapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class PriceData(
    val nokPerKwh: Float,
    val timeStart: String
)

@Composable
fun GraphScreen(navController: NavController) {

    val testRegion = "Region: Oslo / NO1"
    val buttonToday = "I dag"
    val buttonTomorrow = "I morgen"
    val includeText = "Inkluder nettleie, avgifter og mva"

    val testData = listOf(
        PriceData(-0.01625f, "00:00"),
        PriceData(-0.01284f, "01:00"),
        PriceData(-0.01409f, "02:00"),
        PriceData(-0.01454f, "03:00"),
        PriceData(-0.01443f, "04:00"),
        PriceData(-0.01602f, "05:00"),
        PriceData(-0.01182f, "06:00"),
        PriceData(0.00011f, "07:00"),
        PriceData(0.02056f, "08:00"),
        PriceData(0.01977f, "09:00"),
        PriceData(0.02522f, "10:00"),
        PriceData(0.01443f, "11:00"),
        PriceData(0.01534f, "12:00"),
        PriceData(0.01352f, "13:00"),
        PriceData(0.00909f, "14:00"),
        PriceData(0f, "15:00"),
        PriceData(-0.00307f, "16:00"),
        PriceData(-0.00761f, "17:00"),
        PriceData(0.04306f, "18:00"),
        PriceData(0.04431f, "19:00"),
        PriceData(0.03113f, "20:00"),
        PriceData(0.01909f, "21:00"),
        PriceData(0.00068f, "22:00"),
        PriceData(-0.00738f, "23:00")
    )

    Column(modifier = Modifier.padding(top = 100.dp, start = 16.dp, end = 16.dp)) {
        Row(modifier = Modifier.padding(bottom = 5.dp)) {
            Text(text = testRegion)
            Spacer(modifier = Modifier.width(5.dp))
            Box(modifier = Modifier.clickable { navController.navigate("settings") }) {
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
                onClick = {},
                shape = RectangleShape,
                border = BorderStroke(1.dp, Color.Black),
                colors = ButtonDefaults.buttonColors(Color.Black)
            ) {
                Text(buttonToday)
            }
            Button(
                onClick = {},
                shape = RectangleShape,
                border = BorderStroke(1.dp, Color.Black),
                colors = ButtonDefaults.buttonColors(Color.Gray)
            ) {
                Text(buttonTomorrow)
            }
        }

        Box(
            modifier = Modifier
                .aspectRatio(3 / 2f)
                .fillMaxSize()
                .drawBehind {
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
                    val maxPrice = testData.maxOf { it.nokPerKwh }
                    val minPrice = testData.minOf { it.nokPerKwh }
                    val xScale = size.width / (testData.size - 1)
                    val yScale = size.height / (maxPrice - minPrice)
                    val path = generatePath(testData, xScale, yScale, size, minPrice)

                    drawPath(path, Color.Gray, style = Stroke(4.dp.toPx()))
                }
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(x = 125.dp, y = 25.dp) // hardkodet for mockup
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                        .width(60.dp)
                        .height(45.dp),
                    contentAlignment = Alignment.Center

                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = testData[12].timeStart,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            text = "${"%.2f".format(testData[12].nokPerKwh * 100)} øre"
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.padding(top = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = true,
                onCheckedChange = null
            )
            Text(text = includeText, modifier = Modifier.padding(start = 5.dp))
        }
    }
}

private fun generatePath(
    data: List<PriceData>,
    xScale: Float,
    yScale: Float,
    size: Size,
    minPrice: Float
): Path {
    val path = Path()
    data.forEachIndexed { i, priceData ->
        val x = i * xScale
        val y = (size.height - (priceData.nokPerKwh - minPrice) * yScale)
        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    return path
}
