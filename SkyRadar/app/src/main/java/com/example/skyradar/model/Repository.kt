package com.example.skyradar.model

import kotlinx.coroutines.flow.Flow

interface Repository {

     fun getForecastData(latitude: String, longitude: String, units: String, lang: String): Flow<ForecastResponse>
     fun getForecastDataByCityName(cityName: String, units: String, lang: String): Flow<ForecastResponse>
     fun getWeatherData(latitude: String, longitude: String, units: String, lang: String): Flow<WeatherResponse>
     fun getWeatherDataByCityName(cityName: String, units: String, lang: String): Flow<WeatherResponse>
     suspend fun addFavorite(location: DatabasePojo)
     suspend fun removeFavorite(location: DatabasePojo)
     fun getFavoriteLocations(): Flow<List<DatabasePojo>>
     suspend fun insertAlarm(alarm: Alarm)
     fun getAlarms(): Flow<List<Alarm>>
     fun saveLanguage(language: String)
     fun saveUnit(unit: String)
     fun getLanguage(): String?
     fun getUnit(): String?

}