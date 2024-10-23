package com.example.skyradar.network

import com.example.skyradar.model.Root
import retrofit2.Response

object RemoteDataSourceImpl {

    private val apiService = RetrofitInstance.getApiService()

    suspend fun getForecastData(latitude: String, longitude: String, units: String, lang: String): Response<Root> {
        return apiService.getForecastData(latitude, longitude, units, lang)
    }

    suspend fun getForecastDataByCityName(cityName: String, units: String, lang: String): Response<Root> {
        return apiService.getForecastDataByCityName(cityName, units, lang)
    }


}