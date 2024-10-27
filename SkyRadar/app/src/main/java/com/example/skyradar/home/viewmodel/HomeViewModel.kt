package com.example.skyradar.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyradar.model.ForecastResponse
import com.example.skyradar.model.Repository
import com.example.skyradar.model.WeatherResponse
import com.example.skyradar.network.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val _repo: Repository) : ViewModel() {


    private val _forecastData = MutableStateFlow<ResponseStatus<ForecastResponse>>(ResponseStatus.Loading)
    val forecastData: StateFlow<ResponseStatus<ForecastResponse>> get() = _forecastData

    private val _forecastError = MutableStateFlow<String?>(null)
    val forecastError: StateFlow<String?> get() = _forecastError

    private val _weatherData = MutableStateFlow<ResponseStatus<WeatherResponse>>(ResponseStatus.Loading)
    val weatherData: StateFlow<ResponseStatus<WeatherResponse>> get() = _weatherData

    private val _weatherError = MutableStateFlow<String?>(null)
    val weatherError: StateFlow<String?> get() = _weatherError

    // Fetch weather data using latitude and longitude, including units and language
    fun fetchWeatherData(latitude: String, longitude: String, units: String, lang: String) {
        _weatherData.value = ResponseStatus.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _repo.getWeatherData(latitude, longitude, units, lang).collect { weatherDataRoot ->
                    Log.i("TestingApiViewModel", "Weather data fetched successfully: ${weatherDataRoot.name}")

                    // Post the entire Root object wrapped in ResponseStatus.Success
                    _weatherData.value = ResponseStatus.Success(weatherDataRoot)
                }
            } catch (e: Exception) {
                _weatherError.value = "Error while fetching weather data: ${e.message}"
                Log.i("TestingApiViewModel", "Error while fetching weather data: ${e.message}")
            }
        }
    }

    // Fetch weather data by city name, including units and language
    fun fetchWeatherDataByCityName(cityName: String, units: String, lang: String) {
        _weatherData.value = ResponseStatus.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _repo.getWeatherDataByCityName(cityName, units, lang).collect { weatherDataRoot ->
                    Log.i("TestingApiViewModel", "Weather data fetched successfully for city: ${weatherDataRoot.name}")

                    // Post the entire Root object wrapped in ResponseStatus.Success
                    _weatherData.value = ResponseStatus.Success(weatherDataRoot)
                }
            } catch (e: Exception) {
                _weatherError.value = "Error while fetching weather data by city name: ${e.message}"
                Log.i("TestingApiViewModel", "Error while fetching weather data by city name: ${e.message}")
            }
        }
    }

    // Fetch forecast data using latitude and longitude, including units and language
    fun fetchForecastData(latitude: String, longitude: String, units: String, lang: String) {
        _forecastData.value = ResponseStatus.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _repo.getForecastData(latitude, longitude, units, lang).collect { weatherDataRoot ->
                    Log.i("TestingApiViewModel", "Forecast data fetched successfully: ${weatherDataRoot.city.name}")

                    // Post the entire Root object wrapped in ResponseStatus.Success
                    _forecastData.value = ResponseStatus.Success(weatherDataRoot)
                }
            } catch (e: Exception) {
                _forecastError.value = "Error while fetching weather data: ${e.message}"
                Log.i("TestingApiViewModel", "Error while fetching forecast data: ${e.message}")
            }
        }
    }
}
