package com.example.skyradar.model

import kotlinx.coroutines.flow.Flow

interface Repository {

     fun getForecastData(latitude: String, longitude: String, units: String, lang: String): Flow<ForecastResponse>
     fun getForecastDataByCityName(cityName: String, units: String, lang: String): Flow<ForecastResponse>
     fun getWeatherData(latitude: String, longitude: String, units: String, lang: String): Flow<WeatherResponse>
     fun getWeatherDataByCityName(cityName: String, units: String, lang: String): Flow<WeatherResponse>
}