package com.example.skyradar.model

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class ForecastResponse(
    @ColumnInfo(name = "forecast_cod")
    val cod: String? = null,
    val message: Long? = null,
    val cnt: Long? = null,
    val list: List<WeatherList>? = null,
    @Embedded(prefix = "city_")
    val city: City? = null
)















