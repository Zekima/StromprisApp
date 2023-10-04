package com.example.stromprisapp.ui

import com.example.stromprisappui.ui.Global
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stromprisappui.ui.TekstMedBakgrunn
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SimpleDateFormat")
@Preview
@Composable
fun HomeScreen() {
    val litenOverskrift = 20.sp; val pris = 50.sp; val valuta = 17.sp
    val datesize = 16.sp; val paddingMellomOverskrifter = 70.dp

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        TekstMedBakgrunn(
            backgroundColor = Global.bakgrunnsfarge,
            "Strømpriser",
            modifier = Modifier.padding(top = paddingMellomOverskrifter),
            fontSize = 50.sp
        )
        TekstMedBakgrunn(
            backgroundColor = Global.bakgrunnsfarge,
            "Strømprisen idag",
            modifier = Modifier.padding(top = paddingMellomOverskrifter),
            fontSize = litenOverskrift,
            fontWeight = FontWeight.Bold
        )
        TekstMedBakgrunn(
            backgroundColor = Global.bakgrunnsfarge,
            ""+ SimpleDateFormat("dd/MM/yyyy - hh:mm z").format(Date.from(Instant.now())),
            fontSize = datesize
        )
        Row() {
            TekstMedBakgrunn(
                backgroundColor = Global.bakgrunnsfarge,
                "143.5",
                fontSize = pris

            )
            TekstMedBakgrunn(
                backgroundColor = Global.bakgrunnsfarge,
                "øre/kWh",
                modifier = Modifier.padding(top = 32.dp),
                fontSize = valuta
            )
        }
        TekstMedBakgrunn(
            backgroundColor = Global.bakgrunnsfarge,
            "Medianpris for gårsdagen",
            modifier = Modifier.padding(top = paddingMellomOverskrifter),
            fontSize = litenOverskrift,
            fontWeight = FontWeight.Bold
        )
        Row() {
            TekstMedBakgrunn(
                backgroundColor = Global.bakgrunnsfarge,
                "139.7",
                fontSize = pris
            )
            TekstMedBakgrunn(
                backgroundColor = Global.bakgrunnsfarge,
                "øre/kWh",
                modifier = Modifier.padding(top = 32.dp),
                fontSize = valuta
            )
        }
    }
}
