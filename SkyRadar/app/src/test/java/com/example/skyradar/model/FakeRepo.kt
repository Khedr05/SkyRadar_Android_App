package com.example.skyradar.model

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class FakeRepo: Repository {

    private val _forecastData = MutableStateFlow<ForecastResponse?>(null)
    val forecastData: MutableStateFlow<ForecastResponse?> get() = _forecastData


    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: MutableStateFlow<WeatherResponse?> get() = _weatherData


    var forecastResponse: ForecastResponse? = null
    var weatherResponse: WeatherResponse? = null

    // Fetch weather data by latitude and longitude
    override fun getForecastData(
        latitude: String,
        longitude: String,
        units: String,
        lang: String
    ): Flow<ForecastResponse> = flow {
        forecastResponse?.let {
            emit(it)
        } ?: throw Exception("No forecast data available")
    }

    // Fetch weather data by city name
    override fun getForecastDataByCityName(
        cityName: String,
        units: String,
        lang: String
    ): Flow<ForecastResponse> = flow {
        forecastResponse?.let {
            emit(it)
        } ?: throw Exception("No forecast data available for city: $cityName")
    }

    // Fetch weather data by latitude and longitude
    override fun getWeatherData(
        latitude: String,
        longitude: String,
        units: String,
        lang: String
    ): Flow<WeatherResponse> = flow {
        weatherResponse?.let {
            emit(it)
        } ?: throw Exception("No weather data available")
    }

    // Fetch weather data by city name
    override fun getWeatherDataByCityName(
        cityName: String,
        units: String,
        lang: String
    ): Flow<WeatherResponse> = flow {
        weatherResponse?.let {
            emit(it)
        } ?: throw Exception("No weather data available for city: $cityName")
    }

    override suspend fun addFavorite(location: DatabasePojo) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFavorite(location: DatabasePojo) {
        TODO("Not yet implemented")
    }

    override suspend fun updateFavorite(location: DatabasePojo) {
        TODO("Not yet implemented")
    }

    override fun getFavoriteLocations(): Flow<List<DatabasePojo>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override fun getAlarms(): Flow<List<Alarm>> {
        TODO("Not yet implemented")
    }

    override fun saveLanguage(language: String) {
        TODO("Not yet implemented")
    }

    override fun saveUnit(unit: String) {
        TODO("Not yet implemented")
    }

    override fun getLanguage(): String? {
        TODO("Not yet implemented")
    }

    override fun getUnit(): String? {
        TODO("Not yet implemented")
    }
}
