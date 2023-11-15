package com.example.stromprisapp.ui
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.stromprisapp.ui.theme.Purple80
import com.example.stromprisapp.ui.theme.White

/**
 * Hjelpeklasse som lagrer varibaler som brukes i hele prosjektet
 *
 * @property sharedPrefSone SharedPreferences for selektert sone.
 * @property sharedPrefEur SharedPreferences for Euro.
 * @property sharedPrefNOK SharedPreferences for kroner.
 * @property valgtSone selektert sone som en mutable state variabel.
 * @property velgValuta selektert valuta som en mutable state variabel.
 * @property valutaEUR boolean som indikerer om Euro er selektert.
 * @property valutaNOK boolean som indikerer om NOK er selektert.
 */
object Global {

    lateinit var sharedPrefSone: SharedPreferences

    lateinit var sharedPrefEur: SharedPreferences

    lateinit var sharedPrefNOK : SharedPreferences

    var valgtSone by mutableStateOf("velg sone")

    var velgValuta by mutableStateOf("velg valuta")

    var valutaEUR by mutableStateOf(false)
    var valutaNOK by mutableStateOf(false)
}

