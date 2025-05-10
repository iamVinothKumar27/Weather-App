package com.example.myweather

sealed class WeatherFlows {
    object Success : WeatherFlows()
    data class Toast(val msg : String) : WeatherFlows()
}