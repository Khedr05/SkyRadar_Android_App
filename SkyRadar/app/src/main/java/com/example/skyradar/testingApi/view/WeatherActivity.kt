package com.example.skyradar.testingApi.view

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.R
import com.example.skyradar.model.RepositoryImpl
import com.example.skyradar.network.RemoteDataSourceImpl // Ensure you import this class
import com.example.skyradar.testingApi.viewmodel.TestingApiFactory
import com.example.skyradar.testingApi.viewmodel.TestingApiViewModel

class WeatherActivity : AppCompatActivity() {
    private val viewModel: TestingApiViewModel by viewModels {
        TestingApiFactory(RepositoryImpl(RemoteDataSourceImpl)) // Pass RemoteDataSourceImpl here
    }
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

        // Fetch the weather data (replace with your coordinates)
        //viewModel.fetchWeatherData("29.9855617", "31.0992534", "metric", "ar") // Example coordinates for Tokyo

        viewModel.fetchWeatherDataByCityName("Egypt", "metric", "ar")

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.weatherData.observe(this) { weatherDataList ->
            weatherAdapter.submitList(weatherDataList.list)
            cityNameView.text = weatherDataList.city.name

            // Populate new TextViews with weather data
            currentTempValue.text = "${weatherDataList.list[0].main.temp} °C"
            weatherValue.text = weatherDataList.list[0].weather[0].description.capitalize()
            humidityValue.text = "${weatherDataList.list[0].main.humidity}%"
            pressureValue.text = "${weatherDataList.list[0].main.pressure} hPa"
            tempMaxValue.text = "${weatherDataList.list[0].main.tempMax} °C"
            tempMinValue.text = "${weatherDataList.list[0].main.tempMin} °C"
            windSpeedValue.text = "${weatherDataList.list[0].wind.speed} m/s"
            cloudsValue.text = "${weatherDataList.list[0].clouds.all} %"
        }

        viewModel.error.observe(this) { errorMessage ->
            // Handle error (e.g., show a Toast)
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}
