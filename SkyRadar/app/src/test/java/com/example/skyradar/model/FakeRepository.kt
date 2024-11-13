package com.example.skyradar.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository : Repository {

    var weatherResponse: WeatherResponse? = null
    var forecastResponse: ForecastResponse? = null

    override fun getForecastData(latitude: String, longitude: String, units: String, lang: String): Flow<ForecastResponse> {
        return flow {
            if (forecastResponse == null) {
                throw Exception("Simulated error")
            } else {
                emit(forecastResponse!!)
            }
        }
    }

    override fun getForecastDataByCityName(cityName: String, units: String, lang: String): Flow<ForecastResponse> {
        return flow {
            if (forecastResponse == null) {
                throw Exception("Simulated error")
            } else {
                emit(forecastResponse!!)
            }
        }
    }

    override fun getWeatherData(latitude: String, longitude: String, units: String, lang: String): Flow<WeatherResponse> {
        return flow {
            if (weatherResponse == null) {
                throw Exception("Simulated error")
            } else {
                emit(weatherResponse!!)
            }
        }
    }

    override fun getWeatherDataByCityName(cityName: String, units: String, lang: String): Flow<WeatherResponse> {
        return flow {
            if (weatherResponse == null) {
                throw Exception("Simulated error")
            } else {
                emit(weatherResponse!!)
            }
        }
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
