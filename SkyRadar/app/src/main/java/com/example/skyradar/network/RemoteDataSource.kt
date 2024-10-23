package com.example.skyradar.network

import com.example.skyradar.model.ForecastResponse
import com.example.skyradar.model.WeatherResponse
import retrofit2.Response

interface RemoteDataSource {
    suspend fun getForecastData(latitude: String, longitude: String, units: String, lang: String): Response<ForecastResponse>
    suspend fun getForecastDataByCityName(cityName: String, units: String, lang: String): Response<ForecastResponse>
    suspend fun getWeatherData(latitude: String, longitude: String, units: String, lang: String): Response<WeatherResponse>
    suspend fun getWeatherDataByCityName(cityName: String, units: String, lang: String): Response<WeatherResponse>
}