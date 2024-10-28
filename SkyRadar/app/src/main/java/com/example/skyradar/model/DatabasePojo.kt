package com.example.skyradar.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites_table")
data class DatabasePojo(
    @PrimaryKey val locationName: String,
    @Embedded
    val Weather: WeatherResponse,
    @Embedded
    val Forecast: ForecastResponse
)
