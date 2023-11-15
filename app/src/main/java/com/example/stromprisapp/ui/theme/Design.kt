@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.example.stromprisapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Composable funksjon som lager ett Card med runde kanter, tekst og en bakgrunn farge som endres utifra
 * om om det er Light eller Darktheme
 *
 * @param content Hvilket content som skal vises i kortet
 */
@Composable
fun RoundedEdgeCardBody(content: @Composable () -> Unit) {
    val background = if (isSystemInDarkTheme()) {
        CardDefaults.cardColors(Purple40)
    } else {
        CardDefaults.cardColors(Pink80)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp)),
        colors = background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}

/**
 * Samme bare at den skal passe til horizontalt view. Vi har forksjellig modifier
 */
@Composable
fun RoundedEdgeCardBodyHorizontal(content: @Composable () -> Unit) {
    val background = if (isSystemInDarkTheme()) {
        CardDefaults.cardColors(Purple40)
    } else {
        CardDefaults.cardColors(Pink80)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(5.dp)
            .clip(RoundedCornerShape(50.dp)),
        colors = background

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

/**
 * All teksten i applikasjonen
 *
 * @param tekst Teksten som skal vises
 * @param fontSize Skrift størrelsen
 * @param fontWeight Om det skal være tykk skrift
 * @param modifier Style det litt mer som man vil
 */
@Composable
fun TekstMedBakgrunn(
    tekst: String,
    fontSize: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    modifier: Modifier = Modifier
) {
    Text(text = tekst,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = fontSize,
        fontWeight = fontWeight,
        textAlign = TextAlign.Center,
        modifier = modifier)
}