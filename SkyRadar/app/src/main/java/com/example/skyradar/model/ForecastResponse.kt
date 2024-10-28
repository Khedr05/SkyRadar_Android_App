package com.example.skyradar.model

import androidx.room.ColumnInfo
import androidx.room.Embedded


data class ForecastResponse(
    @ColumnInfo(name = "forecast_cod")
    val cod: String,
    val message: Long,
    val cnt: Long,
    val list: List<WeatherList>,
    @Embedded(prefix = "city_")
    val city: City
)














