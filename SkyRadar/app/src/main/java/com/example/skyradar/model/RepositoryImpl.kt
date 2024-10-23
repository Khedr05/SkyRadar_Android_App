package com.example.skyradar.model

import com.example.skyradar.network.RemoteDataSourceImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RepositoryImpl(
    private val remoteDataSource: RemoteDataSourceImpl
) : Repository {

    // Fetch weather data by latitude and longitude
    override fun getForecastData(
        latitude: String,
        longitude: String,
        units: String,
        lang: String
    ): Flow<ForecastResponse> = flow {
        val response = remoteDataSource.getForecastData(latitude, longitude, units, lang)

        if (response.isSuccessful) {
            response.body()?.let { returnedData ->
                emit(returnedData)
            } ?: run {
                throw Exception("Weather data is null")
            }
        } else {
            throw Exception("Failed to fetch forecast data: ${response.errorBody()?.string()}")
        }
    }

    // Fetch weather data by city name
    override fun getForecastDataByCityName(
        cityName: String,
        units: String,
        lang: String
    ): Flow<ForecastResponse> = flow {
        val response = remoteDataSource.getForecastDataByCityName(cityName, units, lang)

        if (response.isSuccessful) {
            response.body()?.let { returnedData ->
                emit(returnedData)
            } ?: run {
                throw Exception("Weather data is null")
            }
        } else {
            throw Exception("Failed to fetch forecast data: ${response.errorBody()?.string()}")
        }
    }

    // Fetch weather data by latitude and longitude
    override fun getWeatherData(
        latitude: String,
        longitude: String,
        units: String,
        lang: String
    ): Flow<WeatherResponse> = flow {

            val response = remoteDataSource.getWeatherData(latitude, longitude, units, lang)
            if (response.isSuccessful) {
                response.body()?.let { returnedData ->
                    emit(returnedData)
                } ?: run {
                    throw Exception("Weather data is null")
                }
            } else {
                throw Exception("Failed to fetch weather data: ${response.errorBody()?.string()}")
            }

    }

    // Fetch weather data by city name
    override fun getWeatherDataByCityName(
        cityName: String,
        units: String,
        lang: String
    ): Flow<WeatherResponse> = flow {
        val response = remoteDataSource.getWeatherDataByCityName(cityName, units, lang)
        if (response.isSuccessful) {
            response.body()?.let { returnedData ->
                emit(returnedData)
            } ?: run {
                throw Exception("Weather data is null")
                }
        } else {
            throw Exception("Failed to fetch weather data: ${response.errorBody()?.string()}")
        }
    }
}
