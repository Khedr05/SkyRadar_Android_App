package com.example.skyradar.model

data class Root(
    val cod: String,
    val message: Long,
    val cnt: Long,
    val list: List<WeatherList>,
    val city: City,
)















