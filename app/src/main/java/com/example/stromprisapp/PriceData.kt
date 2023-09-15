package com.example.stromprisapp

data class PriceData(
    val NOK_per_kWh: Double,
    val EUR_per_kWh: Double,
    val EXR: Double,
    val time_start: String,
    val time_end: String
)
