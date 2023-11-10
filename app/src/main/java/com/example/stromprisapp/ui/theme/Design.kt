@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.example.stromprisapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.unit.dp



@Composable
fun RoundedEdgeCardBody(content: @Composable () -> Unit) {
    val background = if (isSystemInDarkTheme()) {
        CardDefaults.cardColors(DarkGray)
    } else {
        CardDefaults.cardColors(LightGray)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(23.dp)
            .clip(RoundedCornerShape(50.dp)),
        colors = background


    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            content()
        }
    }
}