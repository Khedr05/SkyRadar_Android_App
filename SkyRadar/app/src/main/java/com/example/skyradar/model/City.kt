package com.example.skyradar.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.io.Serializable

data class City(
    @ColumnInfo(name = "city_id")
    val id: Long,
    @ColumnInfo(name = "city_name")
    val name: String,
    @Embedded(prefix = "city_coord_")
    val coord: Coord,
    val country: String,
    val population: Long,
    @ColumnInfo(name = "city_timezone")
    val timezone: Long,
    val sunrise: Long,
    val sunset: Long
): Serializable
