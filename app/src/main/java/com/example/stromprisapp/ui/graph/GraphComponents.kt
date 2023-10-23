package com.example.stromprisapp.ui.graph

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stromprisapp.PriceData
import com.example.stromprisapp.Utils

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

        val orePerKwhValue = selectedDataPoint.nokPerKwh * 100
        val displayPrice = if (includeFees) {
            val feesIncludedValue = Utils.includeFees(orePerKwhValue)
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