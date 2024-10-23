package com.example.skyradar.network

import com.example.skyradar.model.Root
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
        ): Response<Root>

    @GET("forecast?")
    suspend fun getForecastDataByCityName(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en",
        @Query("appid") apiKey: String = RetrofitInstance.API_KEY
        ): Response<Root>
}
