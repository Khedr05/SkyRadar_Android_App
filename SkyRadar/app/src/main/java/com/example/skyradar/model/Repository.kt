package com.example.skyradar.model

interface Repository {

    suspend fun getForecastData(latitude: String, longitude: String, units: String, lang: String): Root
    suspend fun getForecastDataByCityName(cityName: String, units: String, lang: String): Root

}