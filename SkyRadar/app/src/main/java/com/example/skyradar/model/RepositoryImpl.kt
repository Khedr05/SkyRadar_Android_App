package com.example.skyradar.model

import com.example.skyradar.network.RemoteDataSourceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoryImpl(
    private val remoteDataSource: RemoteDataSourceImpl
) : Repository {

    // Fetch weather data by latitude and longitude
    override suspend fun getForecastData(latitude: String, longitude: String, units: String, lang: String): Root = withContext(
        Dispatchers.IO
    ) {
        val responseRet = remoteDataSource.getForecastData(latitude, longitude, units, lang)
        if (responseRet.isSuccessful) {
            responseRet.body() ?: throw Exception("No weather data found")
        } else {
            throw Exception("Failed to fetch weather data from API: ${responseRet.message()}")
        }
    }

    // Fetch weather data by city name
    override suspend fun getForecastDataByCityName(cityName: String, units: String, lang: String): Root = withContext(
        Dispatchers.IO
    ) {
        val responseRet = remoteDataSource.getForecastDataByCityName(cityName, units, lang)
        if (responseRet.isSuccessful) {
            responseRet.body() ?: throw Exception("No weather data found")
        } else {
            throw Exception("Failed to fetch weather data by city name from API: ${responseRet.message()}")
        }
    }
}
