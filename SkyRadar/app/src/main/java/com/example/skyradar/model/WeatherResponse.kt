package com.example.skyradar.model

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class WeatherResponse(
    @Embedded(prefix = "coord_")
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    @Embedded(prefix = "main_")
    val main: Main,
    val visibility: Int,
    @Embedded(prefix = "wind_")
    val wind: Wind,
    @Embedded(prefix = "clouds_")
    val clouds: Clouds,
    val dt: Long,
    @Embedded(prefix = "sys_")
    val sys: Sys,
    @ColumnInfo(name = "weather_timezone")
    val timezone: Int,
    @ColumnInfo(name = "weather_id")
    val id: Int,
    @ColumnInfo(name = "weather_name")
    val name: String,
    @ColumnInfo(name = "weather_cod")
    val cod: Int
)