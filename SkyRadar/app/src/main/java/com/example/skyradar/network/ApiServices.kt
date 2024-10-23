package com.example.skyradar.network

import com.example.skyradar.model.ForecastResponse
import com.example.skyradar.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {

    @GET("forecast?")
    suspend fun getForecastData(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en",
        @Query("appid") apiKey: String = RetrofitInstance.API_KEY
        ): Response<ForecastResponse>

    @GET("forecast?")
    suspend fun getForecastDataByCityName(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en",
        @Query("appid") apiKey: String = RetrofitInstance.API_KEY
        ): Response<ForecastResponse>

    @GET("weather?")
    suspend fun getWeatherData(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en",
        @Query("appid") apiKey: String = RetrofitInstance.API_KEY
    ): Response<WeatherResponse>

    @GET("weather?")
    suspend fun getWeatherDataByCityName(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en",
        @Query("appid") apiKey: String = RetrofitInstance.API_KEY
    ): Response<WeatherResponse>
}
