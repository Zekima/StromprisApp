package com.example.stromprisapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.provider.Settings
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
import com.example.stromprisapp.ui.theme.RoundedEdgeCardBody
import com.example.stromprisapp.ui.theme.RoundedEdgeCardBodyHorizontal

@SuppressLint("UnrememberedMutableState")
@Composable
fun SettingsScreen( ) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val context = LocalContext.current

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

    if (isLandscape) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            TekstMedBakgrunn(
                tekst = "Innstillinger",
                fontSize = 35.sp,
                textColor = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    RoundedEdgeCardBodyHorizontal()
                    {
                        TekstMedBakgrunn(
                            tekst = "Her kan du velge de \n ulike sonene",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(25.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(color = MaterialTheme.colorScheme.onPrimary.toArgb()),
                                    CircleShape
                                )

                        ) {
                            TextButton(onClick = { menyValgSone = true }) {
                                TekstMedBakgrunn(
                                    tekst = Utils.convertZoneCode(valgtSone),
                                    fontSize = 25.sp,
                                    textColor = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            DropdownMenu(
                                expanded = menyValgSone,
                                onDismissRequest = { menyValgSone = false }) {
                                listeSone.forEachIndexed { index, item ->
                                    DropdownMenuItem(
                                        {
                                            TekstMedBakgrunn(tekst = item, fontSize = 20.sp, textColor = MaterialTheme.colorScheme.onBackground)
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

                                        }, trailingIcon = {
                                            Icon(
                                                iconSone,
                                                "",
                                                Modifier.clickable { menyValgSone = !menyValgSone }
                                            )
                                        })
                                }
                            }
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    RoundedEdgeCardBodyHorizontal()
                    {
                        TekstMedBakgrunn(
                            tekst = "Her kan du velge valuta\n",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(25.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(color = MaterialTheme.colorScheme.onPrimary.toArgb()),
                                    CircleShape
                                )
                        )
                        {
                            TextButton(onClick = { menyvalgValuta = true }) {
                                TekstMedBakgrunn(tekst = Utils.getValuta(), fontSize = 25.sp, textColor = MaterialTheme.colorScheme.onBackground)
                            }
                            DropdownMenu(
                                expanded = menyvalgValuta,
                                onDismissRequest = { menyvalgValuta = false }) {
                                listeValuta.forEachIndexed { index, item ->
                                    DropdownMenuItem({
                                        TekstMedBakgrunn(tekst = item, fontSize = 20.sp, textColor = MaterialTheme.colorScheme.onBackground)
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
                                                Modifier.clickable { menyvalgValuta = !menyvalgValuta }
                                            )
                                        })
                                }
                            }
                        }
                    }
                }
            }
             Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Button(onClick = { apneNotifikasjonInstillinger(context) }) {
                        TekstMedBakgrunn(tekst = "Notifikasjon for priser", fontSize = 20.sp)
                    }
                }

        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            TekstMedBakgrunn(
                tekst = "Innstillinger",
                fontSize = 35.sp,
                modifier = Modifier.padding(top = 16.dp),
                textColor = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.padding(16.dp))
            RoundedEdgeCardBody()
            {
                TekstMedBakgrunn(
                    tekst = "Her kan du velge de \n ulike sonene",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(25.dp))
                Box(
                    modifier = Modifier
                        .background(
                            Color(color = MaterialTheme.colorScheme.onPrimary.toArgb()),
                            CircleShape
                        )
                )
                {
                    TextButton(onClick = { menyValgSone = true }) {
                        TekstMedBakgrunn(
                            tekst = Utils.convertZoneCode(valgtSone),
                            fontSize = 25.sp,
                            textColor = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    DropdownMenu(
                        expanded = menyValgSone,
                        onDismissRequest = { menyValgSone = false }) {
                        listeSone.forEachIndexed { index, item ->
                            DropdownMenuItem(
                                {
                                    TekstMedBakgrunn(tekst = item, fontSize = 20.sp, textColor = MaterialTheme.colorScheme.onBackground)
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

                                }, trailingIcon = {
                                    Icon(
                                        iconSone,
                                        "",
                                        Modifier.clickable { menyValgSone = !menyValgSone }
                                    )
                                })
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.padding(16.dp))
            RoundedEdgeCardBody()
            {
                TekstMedBakgrunn(
                    tekst = "Her kan du velge valuta",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(30.dp))
                Box(
                    modifier = Modifier
                        .background(
                            Color(color = MaterialTheme.colorScheme.onPrimary.toArgb()),
                            CircleShape
                        )
                )
                {
                    TextButton(onClick = { menyvalgValuta = true }) {
                        TekstMedBakgrunn(tekst = Utils.getValuta(), fontSize = 35.sp, textColor = MaterialTheme.colorScheme.onBackground)
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
                                        Modifier.clickable { menyvalgValuta = !menyvalgValuta }
                                    )
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
                    Button(onClick = { apneNotifikasjonInstillinger(context) }) {
                        TekstMedBakgrunn(tekst = "Notifikasjon for priser", fontSize = 20.sp)
                    }

                }
            Spacer(modifier = Modifier.padding(50.dp))
        }
    }
}

fun apneNotifikasjonInstillinger(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    context.startActivity(intent)
}


@Composable
fun TekstMedBakgrunn(
    tekst: String,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    fontSize: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    modifier: Modifier = Modifier


) {
    Text(text = tekst,
        color = textColor,
        fontSize = fontSize,
        fontWeight = fontWeight,
        textAlign = TextAlign.Center,
        modifier = modifier)

}



