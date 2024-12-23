package com.example.skyradar.model

import com.example.skyradar.database.LocationLocalDataSource
import com.example.skyradar.network.RemoteDataSourceImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.skyradar.MyApplication
import com.example.skyradar.database.AlarmLocalDataSource
import com.example.skyradar.network.RemoteDataSource

class RepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocationLocalDataSource,
    private val alarmLocalDataSource: AlarmLocalDataSource,
    private val sharedPreferences: SharedPreferences
) : Repository {

//    private val sharedPreferences: SharedPreferences = MyApplication.context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

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

    override suspend fun addFavorite(location: DatabasePojo) {
        localDataSource.addFavorite(location)
    }

    override suspend fun removeFavorite(location: DatabasePojo) {
        localDataSource.removeFavorite(location)
    }

    override suspend fun updateFavorite(location: DatabasePojo) {
        localDataSource.updateFavorite(location)
    }

    override fun getFavoriteLocations(): Flow<List<DatabasePojo>> {
        return localDataSource.getFavoriteLocations()
    }

    override suspend fun insertAlarm(alarm: Alarm) {
        alarmLocalDataSource.insertAlarm(alarm)
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        alarmLocalDataSource.deleteAlarm(alarm)
    }

    override fun getAlarms(): Flow<List<Alarm>> {
        return alarmLocalDataSource.getAlarms()
    }

    override fun saveLanguage(language: String) {
        sharedPreferences.edit {
            putString("language", language)
        }
    }

    override fun saveUnit(unit: String) {
        sharedPreferences.edit {
            putString("unit", unit)
        }
    }

    override fun getLanguage(): String? {
        return sharedPreferences.getString("language", "Mobile Language")
    }

    override fun getUnit(): String? {
        return sharedPreferences.getString("unit", "Metric")
    }
}
