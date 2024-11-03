package com.example.skyradar.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "favorites_table")
data class DatabasePojo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @Embedded
    var Weather: WeatherResponse,
    @Embedded
    var Forecast: ForecastResponse
): Serializable
