package com.example.stromprisappui.ui

import com.example.stromprisappui.ui.Global.bakgrunnsfarge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stromprisappui.ui.theme.Black
import com.example.stromprisappui.ui.theme.White


@Composable
fun SettingsScreen( ) {

    var menyvalgValuta by remember {
        mutableStateOf(false)
    }
    var menyValgSone by remember {
        mutableStateOf(false)
    }
    val listeValuta = listOf("NOK", "€", "£", "$")

    val listeSone = listOf("Oslo / Øst-Norge", "Kristiandsand /Sør-Norge", "Trondheim / Midt-Norge",
        "Tromsø / Nord-Norge", "Bergen / Vest-Norge")

    val iconSone = if( menyValgSone) {
        Icons.Filled.KeyboardArrowDown
    } else {
        Icons.Filled.KeyboardArrowUp
    }
    val iconValuta = if( menyvalgValuta) {
        Icons.Filled.KeyboardArrowDown
    } else {
        Icons.Filled.KeyboardArrowUp
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Settings", fontSize = 40.sp, modifier = Modifier.padding(16.dp))


        Button(
            onClick = { }
        ) {
            Text(text = "Lys modus")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(onClick = { menyValgSone = true } ) {

                Text("velg sone")
            }
            DropdownMenu(expanded = menyValgSone, onDismissRequest = { menyValgSone = false } ) {
                listeSone.forEachIndexed { index, item ->
                    DropdownMenuItem({
                        Text(text = item)
                    }, onClick = { },
                        modifier = Modifier.background(Color.LightGray),
                        trailingIcon = {
                            Icon(iconSone, "", Modifier.clickable { menyValgSone = !menyValgSone})
                        })
                }
            }

            Button(onClick = { menyvalgValuta = true } ) {
                Text(text = "velg valuta")
            }
            DropdownMenu(expanded = menyvalgValuta, onDismissRequest = { menyvalgValuta = false}, offset = DpOffset((-16).dp, (-16).dp)) {
                listeValuta.forEachIndexed { index, item ->
                    DropdownMenuItem({
                        Text(text = item)
                    }, onClick = { },
                        modifier = Modifier.background(Color.LightGray),
                        trailingIcon = {
                            Icon(iconValuta, "", Modifier.clickable { menyvalgValuta = !menyvalgValuta})
                        })
                }

            }
        }
    }
}

@Composable
fun TekstMedBakgrunn(
    backgroundColor: Color = bakgrunnsfarge,
    tekst: String,
    fontSize: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    modifier: Modifier = Modifier

) {
    val farge = kontrastFarge(backgroundColor)

   Text(text = tekst,
       color = farge,
       fontSize = fontSize,
       fontWeight = fontWeight)
}

fun kontrastFarge(backgroundColor: Color, modifier: Modifier = Modifier): Color {

    val farge = (0.299 * backgroundColor.red + 0.587 * backgroundColor.green + 0.114 * backgroundColor.blue).toFloat()

   return if(farge > 0.5){
        Black
    } else {
        White
    }
}
    
