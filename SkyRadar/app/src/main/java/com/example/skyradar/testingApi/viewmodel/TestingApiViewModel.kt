package com.example.skyradar.testingApi.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyradar.model.Repository
import com.example.skyradar.model.Root // Now handling the entire Root object
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestingApiViewModel(private val _repo: Repository) : ViewModel() {

    // Update MutableLiveData to hold the entire Root object
    private val _weatherData = MutableLiveData<Root>()
    val weatherData: LiveData<Root> get() = _weatherData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Fetch weather data using latitude and longitude, including units and language
    fun fetchWeatherData(latitude: String, longitude: String, units: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weatherDataRoot = _repo.getForecastData(latitude, longitude, units, lang)
                Log.i("TestingApiViewModel", "Weather data fetched successfully: ${weatherDataRoot.city.name}")

                // Post the entire Root object
                _weatherData.postValue(weatherDataRoot)
            } catch (e: Exception) {
                _error.postValue("Error while fetching weather data: ${e.message}")
                Log.i("TestingApiViewModel", "Error while fetching weather data: ${e.message}")
            }
        }
    }

    // Fetch weather data by city name, including units and language
    fun fetchWeatherDataByCityName(cityName: String, units: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weatherDataRoot = _repo.getForecastDataByCityName(cityName, units, lang)
                Log.i("TestingApiViewModel", "Weather data fetched successfully for city: ${weatherDataRoot.city.name}")

                // Post the entire Root object
                _weatherData.postValue(weatherDataRoot)
            } catch (e: Exception) {
                _error.postValue("Error while fetching weather data by city name: ${e.message}")
                Log.i("TestingApiViewModel", "Error while fetching weather data by city name: ${e.message}")
            }
        }
    }
}
