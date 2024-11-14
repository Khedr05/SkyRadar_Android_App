package com.example.skyradar.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository : Repository {

    var weatherResponse: WeatherResponse? = null
    var forecastResponse: ForecastResponse? = null

    private val favoriteLocations = mutableListOf<DatabasePojo>()
    private val favoriteLocationsFlow = flow { emit(favoriteLocations) }

    override fun getForecastData(latitude: String, longitude: String, units: String, lang: String): Flow<ForecastResponse> {
        return flow {
            forecastResponse?.let { emit(it) } ?: emit(forecastResponse!!) // Emit thfault empty Weather=====
        }
    }

    override fun getForecastDataByCityName(cityName: String, units: String, lang: String): Flow<ForecastResponse> {
        return flow {
            forecastResponse?.let { emit(it) } ?: emit(forecastResponse!!) // Emit thfault empty Weather=====
        }
    }

    override fun getWeatherData(latitude: String, longitude: String, units: String, lang: String): Flow<WeatherResponse> {
        return flow {
            weatherResponse?.let { emit(it) } ?: emit(weatherResponse!!) // Emit thfault empty Weather=====
        }
    }

    override fun getWeatherDataByCityName(cityName: String, units: String, lang: String): Flow<WeatherResponse> {
        return flow {
            weatherResponse?.let { emit(it) } ?: emit(weatherResponse!!) // Emit thfault empty Weather=====
        }
    }


    // Implement the method to add a location to favorites
    override suspend fun addFavorite(location: DatabasePojo) {
        favoriteLocations.add(location)
    }

    // Implement the method to remove a location from favorites
    override suspend fun removeFavorite(location: DatabasePojo) {
        favoriteLocations.remove(location)
    }

    // Implement the method to update a favorite location
    override suspend fun updateFavorite(location: DatabasePojo) {
        val index = favoriteLocations.indexOfFirst { it.id == location.id }
        if (index != -1) {
            favoriteLocations[index] = location
        } else {
            favoriteLocations.add(location)
        }
    }

    // Implement the method to retrieve favorite locations
    override fun getFavoriteLocations(): Flow<List<DatabasePojo>> {
        return flow { emit(favoriteLocations) }
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
