package com.example.stromprisapp.ui
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.stromprisapp.ui.theme.Purple80
import com.example.stromprisapp.ui.theme.White


object Global {

    var isDarkMode by mutableStateOf(false)

    val bakgrunnsfarge: Color
        get() = if (isDarkMode) {
            Purple80
        } else {
            White
        }
     lateinit var sharedPrefSone: SharedPreferences

    lateinit var sharedPrefEur: SharedPreferences

     lateinit var sharedPrefNOK : SharedPreferences

    var valgtSone by mutableStateOf("velg sone")

    var velgValuta by mutableStateOf("velg valuta")

    var valutaEUR by mutableStateOf(false)
    var valutaNOK by mutableStateOf(false)
}
