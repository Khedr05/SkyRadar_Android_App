package com.example.skyradar.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromWeatherList(value: List<Weather>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toWeatherList(value: String): List<Weather> {
        val listType = object : TypeToken<List<Weather>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromWeatherListList(value: List<WeatherList>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toWeatherListList(value: String): List<WeatherList> {
        val listType = object : TypeToken<List<WeatherList>>() {}.type
        return gson.fromJson(value, listType)
    }
}
