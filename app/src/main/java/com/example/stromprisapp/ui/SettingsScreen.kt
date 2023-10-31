package com.example.stromprisapp.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stromprisapp.Utils
import com.example.stromprisapp.ui.Global.sharedPrefEur
import com.example.stromprisapp.ui.Global.sharedPrefNOK
import com.example.stromprisapp.ui.Global.sharedPrefSone
import com.example.stromprisapp.ui.Global.valgtSone
import com.example.stromprisapp.ui.Global.valutaEUR
import com.example.stromprisapp.ui.Global.valutaNOK
import com.example.stromprisapp.ui.Global.velgValuta
import com.example.stromprisapp.ui.theme.Black
import com.example.stromprisapp.ui.theme.White

@Composable
fun SettingsScreen( ) {

    var menyvalgValuta by remember {
        mutableStateOf(false)
    }
    var menyValgSone by remember {
        mutableStateOf(false)
    }
    val listeValuta = listOf("NOK", "€")

    val listeSone = listOf("Oslo Øst-Norge", "Kristiandsand Sør-Norge", "Trondheim Midt-Norge",
        "Tromsø Nord-Norge", "Bergen Vest-Norge")

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
        TekstMedBakgrunn(tekst = "Settings", fontSize = 50.sp, modifier = Modifier.padding(top =16.dp, bottom = 90.dp) )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            // verticalAlignment = Alignment.CenterVertically
        ) {

            TekstMedBakgrunn(tekst = "Her kan du velge de \n ulike sonene", fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .background(Color(color = MaterialTheme.colorScheme.primary.toArgb()), CircleShape)

            ) {

                Button(onClick = { menyValgSone = true }) {
                    TekstMedBakgrunn(tekst = convertZoneCode(valgtSone), fontSize = 25.sp)
                }
                DropdownMenu(expanded = menyValgSone, onDismissRequest = { menyValgSone = false }) {
                    listeSone.forEachIndexed { index, item ->
                        DropdownMenuItem({
                            TekstMedBakgrunn(tekst = item, fontSize = 20.sp)
                        }, onClick = {
                            val element = listeSone[index]
                            valgtSone = when (element) {
                                "Oslo Øst-Norge" -> "NO1"
                                "Kristiandsand Sør-Norge" -> "NO2"
                                "Trondheim Midt-Norge" -> "NO3"
                                "Tromsø Nord-Norge" -> "NO4"
                                "Bergen Vest-Norge" -> "NO5"
                                else -> "Finner ikke valgt sone"
                            }
                            val editor = sharedPrefSone.edit()
                            editor.putString("valgtSone", valgtSone)
                            editor.apply()
                            menyValgSone = false
                            valgtSone = element

                        },
                            trailingIcon = {
                                Icon(
                                    iconSone,
                                    "",
                                    Modifier.clickable { menyValgSone = !menyValgSone })
                            })
                    }
                }
            }

            Spacer(modifier = Modifier.height(150.dp))
            TekstMedBakgrunn(tekst = "Her kan du velge valuta", fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    // .width(100.dp)
                    // .height(100.dp)
                    .background(Color(color = MaterialTheme.colorScheme.primary.toArgb()), CircleShape)
            ) {
                Button(onClick = { menyvalgValuta = true }) {
                    TekstMedBakgrunn(tekst = Utils.getValuta(), fontSize = 35.sp)
                }
                DropdownMenu(
                    expanded = menyvalgValuta,
                    onDismissRequest = { menyvalgValuta = false }) {
                    listeValuta.forEachIndexed { index, item ->
                        DropdownMenuItem({
                            TekstMedBakgrunn(tekst = item, fontSize = 20.sp)
                        }, onClick = {
                            when (listeValuta[index]) {
                                "NOK" -> {
                                    valutaNOK = true
                                    valutaEUR = false
                                }
                                "€" -> {
                                    valutaEUR = true
                                    valutaNOK = false
                                }

                                else -> "ugyldig Valuta"
                            }
                            val endre = sharedPrefNOK.edit()
                            endre.putBoolean("valutaNOK", valutaNOK)
                            endre.apply()
                            val endre1 = sharedPrefEur.edit()
                            endre1.putBoolean("valutaEUR", valutaEUR)
                            endre1.apply()
                            menyvalgValuta = false

                            velgValuta = listeValuta[index]
                        },
                            trailingIcon = {
                                Icon(
                                    iconValuta,
                                    "",
                                    Modifier.clickable { menyvalgValuta = !menyvalgValuta })
                            })
                    }

                }
            }
        }
    }
}

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
       modifier = modifier)

}

fun convertZoneCode(s : String) : String {
    var b = when(s) {
        "NO1"  -> "Oslo " + "Øst-Norge"
        "NO2" -> "Kristiandsand \n \n Sør-Norge"
        "NO3" -> "Trondheim \n \n Midt-Norge"
        "NO4" -> "Tromsø \n \n Nord-Norge"
        "NO5" -> "Bergen \n \n Vest-Norge"
        else -> "velg sone"
    }
    return b;
}


fun kontrastFarge(backgroundColor: Color, modifier: Modifier = Modifier): Color {

    val farge = (0.299 * backgroundColor.red + 0.587 * backgroundColor.green + 0.114 * backgroundColor.blue).toFloat()

   return if(farge > 0.5){
        Black
    } else {
        White
    }
}
    
