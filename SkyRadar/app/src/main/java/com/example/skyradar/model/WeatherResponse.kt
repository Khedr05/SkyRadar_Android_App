package com.example.skyradar.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.io.Serializable

data class WeatherResponse(
    @Embedded(prefix = "coord_")
    val coord: Coord? = null,
    val weather: List<Weather>? = null,
    val base: String? = null,
    @Embedded(prefix = "main_")
    val main: Main? = null,
    val visibility: Int? = null,
    @Embedded(prefix = "wind_")
    val wind: Wind? = null,
    @Embedded(prefix = "clouds_")
    val clouds: Clouds? = null,
    val dt: Long? = null,
    @Embedded(prefix = "sys_")
    val sys: Sys? = null,
    @ColumnInfo(name = "weather_timezone")
    val timezone: Int? = null,
    @ColumnInfo(name = "weather_id")
    val id: Int? = null,
    @ColumnInfo(name = "weather_name")
    val name: String? = null,
    @ColumnInfo(name = "weather_cod")
    val cod: Int? = null
) : Serializable
