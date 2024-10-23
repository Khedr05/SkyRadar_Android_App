package com.example.skyradar.network

import com.example.skyradar.model.ForecastResponse
import com.example.skyradar.model.WeatherResponse
import retrofit2.Response

class RemoteDataSourceImpl(private val apiService: ApiServices) : RemoteDataSource {

    override suspend fun getForecastData(latitude: String, longitude: String, units: String, lang: String): Response<ForecastResponse> {
        return apiService.getForecastData(latitude, longitude, units, lang)
    }

    override suspend fun getForecastDataByCityName(cityName: String, units: String, lang: String): Response<ForecastResponse> {
        return apiService.getForecastDataByCityName(cityName, units, lang)
    }

    override suspend fun getWeatherData(latitude: String, longitude: String, units: String, lang: String): Response<WeatherResponse> {
        return apiService.getWeatherData(latitude, longitude, units, lang)
    }

    override suspend fun getWeatherDataByCityName(cityName: String, units: String, lang: String): Response<WeatherResponse> {
        return apiService.getWeatherDataByCityName(cityName, units, lang)
    }


    companion object {
        @Volatile
        private var instance: RemoteDataSourceImpl? = null

        fun getInstance(apiService: ApiServices): RemoteDataSourceImpl {
            return instance ?: synchronized(this) {
                instance ?: RemoteDataSourceImpl(apiService).also { instance = it }
            }
        }
    }

}