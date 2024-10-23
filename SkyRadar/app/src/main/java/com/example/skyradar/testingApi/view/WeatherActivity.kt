package com.example.skyradar.testingApi.view

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.R
import com.example.skyradar.model.ForecastResponse
import com.example.skyradar.model.RepositoryImpl
import com.example.skyradar.model.WeatherResponse
import com.example.skyradar.network.RemoteDataSourceImpl // Ensure you import this class
import com.example.skyradar.network.ResponseStatus
import com.example.skyradar.network.RetrofitInstance
import com.example.skyradar.testingApi.viewmodel.TestingApiFactory
import com.example.skyradar.testingApi.viewmodel.TestingApiViewModel
import kotlinx.coroutines.launch

class WeatherActivity : AppCompatActivity() {
//    private val viewModel: TestingApiViewModel by viewModels {
//        TestingApiFactory(RepositoryImpl(RemoteDataSourceImpl)) // Pass RemoteDataSourceImpl here
//    }
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var cityNameView: TextView
    private lateinit var currentTempValue: TextView
    private lateinit var weatherValue: TextView
    private lateinit var humidityValue: TextView
    private lateinit var pressureValue: TextView
    private lateinit var tempMaxValue: TextView
    private lateinit var tempMinValue: TextView
    private lateinit var windSpeedValue: TextView
    private lateinit var cloudsValue: TextView
    private lateinit var viewModel: TestingApiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        recyclerView = findViewById(R.id.recyclerView)
        cityNameView = findViewById(R.id.textView)
        currentTempValue = findViewById(R.id.currentTempValue);
        weatherValue = findViewById(R.id.weatherValue);
        humidityValue = findViewById(R.id.humidityValue);
        pressureValue = findViewById(R.id.pressureValue);
        tempMaxValue = findViewById(R.id.tempMaxValue);
        tempMinValue = findViewById(R.id.tempMinValue);
        windSpeedValue = findViewById(R.id.windSpeedValue);
        cloudsValue = findViewById(R.id.cloudsValue);


        weatherAdapter = WeatherAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = weatherAdapter

        val factory = TestingApiFactory(RepositoryImpl(RemoteDataSourceImpl.getInstance(
            RetrofitInstance.retrofit)))

        viewModel = ViewModelProvider(this, factory).get(TestingApiViewModel::class.java)

        // Fetch the weather data (replace with your coordinates)
        //viewModel.fetchWeatherData("29.9855617", "31.0992534", "metric", "ar") // Example coordinates for Tokyo

        observeViewModel()
    }


    private fun observeViewModel() {
        // Collect weather data state
        lifecycleScope.launch {
            viewModel.forecastData.collect { state ->
                when (state) {
                    is ResponseStatus.Loading -> {
                        // Handle loading state (e.g., show a progress bar)
                    }

                    is ResponseStatus.Success<*> -> {
                        when (val requestedData = state.requestedData) {
                            is ForecastResponse -> {
                                // Handle ForecastResponse data
                                weatherAdapter.submitList(requestedData.list)
                                cityNameView.text = requestedData.city.name
                                currentTempValue.text = "${requestedData.list[0].main.temp} °C"
                                weatherValue.text = requestedData.list[0].weather[0].description.capitalize()
                                humidityValue.text = "${requestedData.list[0].main.humidity}%"
                                pressureValue.text = "${requestedData.list[0].main.pressure} hPa"
                                tempMaxValue.text = "${requestedData.list[0].main.tempMax} °C"
                                tempMinValue.text = "${requestedData.list[0].main.tempMin} °C"
                                windSpeedValue.text = "${requestedData.list[0].wind.speed} m/s"
                                cloudsValue.text = "${requestedData.list[0].clouds.all} %"
                            }
                            is WeatherResponse -> {
                                // Handle WeatherResponse data
                                cityNameView.text = requestedData.name
                                currentTempValue.text = "${requestedData.main.temp} °C"
                                weatherValue.text = requestedData.weather[0].description.capitalize()
                                humidityValue.text = "${requestedData.main.humidity}%"
                                pressureValue.text = "${requestedData.main.pressure} hPa"
                                tempMaxValue.text = "${requestedData.main.tempMax} °C"
                                tempMinValue.text = "${requestedData.main.tempMin} °C"
                                windSpeedValue.text = "${requestedData.wind.speed} m/s"
                                cloudsValue.text = "${requestedData.clouds.all} %"
                            }
                        }
                    }

                    is ResponseStatus.Failure -> {
                        Toast.makeText(this@WeatherActivity, "Error: ${state.errorMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
       // viewModel.fetchWeatherDataByCityName("Egypt", "metric", "ar")
            viewModel.fetchWeatherData("29.9855617", "31.0992534", "metric", "en")
        //viewModel.fetchForecastData("29.9855617", "31.0992534", "metric", "en")
    }
}
