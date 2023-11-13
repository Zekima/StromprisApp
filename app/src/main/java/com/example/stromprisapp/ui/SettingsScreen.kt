package com.example.stromprisapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@SuppressLint("UnrememberedMutableState")
@Composable
fun SettingsScreen( ) {

    var isCkecked by mutableStateOf(false)

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val currentChecked = LocalContext.current;
    val sharedPrefChecked = currentChecked.getSharedPreferences("minPrefChecked", Context.MODE_PRIVATE)
    isCkecked = sharedPrefChecked.getBoolean("ischecked",false)

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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TekstMedBakgrunn(tekst = "Settings", fontSize = 50.sp, modifier = Modifier.padding(top =16.dp))
            Divider(color = Color.Black)

        }
        Spacer(modifier = Modifier.padding(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Color(color = MaterialTheme.colorScheme.secondary.toArgb()),
                        shape = RoundedCornerShape(16.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally


            ) {

                TekstMedBakgrunn(tekst = "Her kan du velge de \n ulike sonene", fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(40.dp))
                Box(
                    modifier = Modifier
                        .background(Color(color = MaterialTheme.colorScheme.onPrimary.toArgb()), CircleShape)

                ) {

                    Button(onClick = { menyValgSone = true }) {
                        TekstMedBakgrunn(tekst = Utils.convertZoneCode(valgtSone), fontSize = 25.sp)
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
            }

            Spacer(modifier = Modifier.height(150.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Color(color = MaterialTheme.colorScheme.secondary.toArgb()),
                        shape = RoundedCornerShape(16.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {

                TekstMedBakgrunn(tekst = "Her kan du velge valuta", fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(50.dp))
                Box(
                    modifier = Modifier

                        // .height(100.dp)
                        .background(Color(color = MaterialTheme.colorScheme.primary.toArgb()), CircleShape)
                ) {
                    Button(onClick = { menyvalgValuta = true }) {
                        TekstMedBakgrunn(tekst = Utils.getValuta(), fontSize = 35.sp)
                    }
                    DropdownMenu(
                        expanded = menyvalgValuta,
                        onDismissRequest = { menyvalgValuta = false } ) {
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
            Spacer(modifier = Modifier.padding(16.dp))
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = isCkecked, onCheckedChange = {
                    isCkecked = it
                    val endre = sharedPrefChecked.edit()
                    endre.putBoolean("ischecked",isCkecked)
                    endre.apply()})

                TekstMedBakgrunn(tekst = "Notifikasjon", fontSize = 16.sp)
            }
               Spacer(modifier = Modifier.padding(50.dp))
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
       textAlign = TextAlign.Center,
       modifier = modifier)

}


fun kontrastFarge(backgroundColor: Color, modifier: Modifier = Modifier): Color {

    val farge = (0.299 * backgroundColor.red + 0.587 * backgroundColor.green + 0.114 * backgroundColor.blue).toFloat()

   return if(farge > 0.5){
        Black
    } else {
        White
    }
}
    
