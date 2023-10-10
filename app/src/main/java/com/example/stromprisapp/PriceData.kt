package com.example.stromprisapp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PriceData(
    @SerialName("NOK_per_kWh") val nokPerKwh: Double,
    @SerialName("EUR_per_kWh") val eurPerKwh: Double,
    @SerialName("EXR") val exr: Double,
    @SerialName("time_start") val timeStart: String,
    @SerialName("time_end") val timeEnd: String
)
