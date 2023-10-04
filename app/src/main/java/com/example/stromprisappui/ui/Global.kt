package com.example.stromprisappui.ui
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.stromprisappui.ui.theme.Purple80
import com.example.stromprisappui.ui.theme.White


object Global {

    var isDarkMode by mutableStateOf(false)

    val bakgrunnsfarge: Color
        get() = if (isDarkMode) {
            Purple80
        } else {
            White
        }

    var valgtSone by mutableStateOf("")

    var velgValuta by mutableStateOf("velg valuta")

    var velgSone by mutableStateOf("velg sone")

    var valutaEUR by mutableStateOf(false)
    var valutaNOK by mutableStateOf(false)
}
