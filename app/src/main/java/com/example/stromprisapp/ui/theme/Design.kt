@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.example.stromprisapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

@Composable
fun RoundedEdgeCardBody(content: @Composable () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(Color(color = MaterialTheme.colorScheme.primary.toArgb())),


    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}
@Composable
fun RoundedEdgeCardBodyHorizontal(content: @Composable () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .padding(5.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color(color = MaterialTheme.colorScheme.primary.toArgb())),

    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
                 // Apply weight here
                verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}



