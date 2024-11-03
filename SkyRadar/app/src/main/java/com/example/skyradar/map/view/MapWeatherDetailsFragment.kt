package com.example.skyradar.map.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.Helpers.formatTimestamp
import com.example.skyradar.Helpers.getCurrentDate
import com.example.skyradar.R
import com.example.skyradar.database.AlarmLocalDataSourceImpl
import com.example.skyradar.database.LocationLocalDataSourceImpl
import com.example.skyradar.home.view.DailyAdapter
import com.example.skyradar.model.ForecastResponse
import com.example.skyradar.model.RepositoryImpl
import com.example.skyradar.model.WeatherResponse
import com.example.skyradar.network.RemoteDataSourceImpl
import com.example.skyradar.network.ResponseStatus
import com.example.skyradar.network.RetrofitInstance
import com.example.skyradar.home.viewmodel.HomeFactory
import com.example.skyradar.home.viewmodel.HomeViewModel
import com.example.skyradar.home.view.HomeAdapter
import com.example.skyradar.home.view.HourlyAdapter
import com.example.skyradar.model.DatabasePojo
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MapWeatherDetailsFragment : Fragment(){


    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var rvHourly: RecyclerView
    private lateinit var rvDaily: RecyclerView
    private lateinit var viewModel: HomeViewModel
    private val uiElements: MutableMap<String, TextView> = mutableMapOf()
    private lateinit var addToFavouritesButton: ImageView

    private var cityWeatherDetails: DatabasePojo? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_map_weather_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI(view)
        initializeViewModel()
        setupRecyclerView()
        observeViewModel()

        val latitude = arguments?.getDouble("LATITUDE")
        val longitude = arguments?.getDouble("LONGITUDE")


        // Fetch weather and forecast data with retrieved latitude and longitude
        if (latitude != null && longitude != null) {
            viewModel.fetchWeatherData(latitude.toString(), longitude.toString(), "metric", "en")
            viewModel.fetchForecastData(latitude.toString(), longitude.toString(), "metric", "en")
        } else {
            showSnackbar("Error: Invalid location data.")
        }

    }

    private fun initializeUI(view: View) {
        addToFavouritesButton = view.findViewById(R.id.btn_favourites)
        rvHourly = view.findViewById(R.id.rv_hourly_degrees)
        rvDaily = view.findViewById(R.id.rv_detailed_days)
        uiElements["cityName"] = view.findViewById(R.id.tv_city_name)
        uiElements["date"] = view.findViewById(R.id.tv_date)
        uiElements["currentTempValue"] = view.findViewById(R.id.tv_current_degree)
        uiElements["weatherValue"] = view.findViewById(R.id.tv_weather_status)
        uiElements["tempMaxValue"] = view.findViewById(R.id.tv_temp_max)
        uiElements["tempMinValue"] = view.findViewById(R.id.tv_temp_min)
        uiElements["humidityValue"] = view.findViewById(R.id.tv_humidity_value)
        uiElements["pressureValue"] = view.findViewById(R.id.tv_pressure_value)
        uiElements["windSpeedValue"] = view.findViewById(R.id.tv_wind_value)
        uiElements["cloudsValue"] = view.findViewById(R.id.tv_cloud_value)
        uiElements["sunriseValue"] = view.findViewById(R.id.tv_sunrise_value)
        uiElements["sunsetValue"] = view.findViewById(R.id.tv_sunset_value)

        addToFavouritesButton.setOnClickListener {
            // Handle the button click event
            if (cityWeatherDetails != null) {
                // Save cityWeatherDetails to favorites or handle the favorite action
                viewModel.addFavoriteLocation(cityWeatherDetails!!)
                showSnackbar("Added to favorites")
            } else {
                showSnackbar("Error: No weather details available to add.")
            }
        }
    }

    private fun initializeViewModel() {
        val factory = HomeFactory(
            RepositoryImpl(RemoteDataSourceImpl.getInstance(RetrofitInstance.retrofit),
                LocationLocalDataSourceImpl.getInstance(requireContext()),
                AlarmLocalDataSourceImpl.getInstance(requireContext()))
        )
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
    }

    private fun setupRecyclerView() {
        hourlyAdapter = HourlyAdapter()
        rvHourly.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvHourly.adapter = hourlyAdapter
        dailyAdapter = DailyAdapter()
        rvDaily.layoutManager = LinearLayoutManager(requireContext())
        rvDaily.adapter = dailyAdapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.weatherData.collect { state -> handleWeatherResponse(state) }
        }
        lifecycleScope.launch {
            viewModel.forecastData.collect { state -> handleForecastResponse(state) }
        }
    }

    private fun handleWeatherResponse(state: ResponseStatus<*>) {
        when (state) {
            is ResponseStatus.Loading -> {}
            is ResponseStatus.Success<*> -> {
                when (val requestedData = state.requestedData) {
                    is WeatherResponse -> updateWeatherUI(requestedData)
                }
            }
            is ResponseStatus.Failure -> showSnackbar("Error: ${state.errorMessage}")
        }
    }

    private fun handleForecastResponse(state: ResponseStatus<*>) {
        when (state) {
            is ResponseStatus.Loading -> {}
            is ResponseStatus.Success<*> -> {
                when (val requestedData = state.requestedData) {
                    is ForecastResponse -> updateForecastUI(requestedData)
                }
            }
            is ResponseStatus.Failure -> showSnackbar("Error: ${state.errorMessage}")
        }
    }

    private fun updateForecastUI(requestedData: ForecastResponse) {
        requestedData.list?.let { dailyAdapter.submitWeatherList(it) }
        requestedData.list?.let { hourlyAdapter.submitTodayWeather(it) }
        if (cityWeatherDetails == null) {
            cityWeatherDetails = DatabasePojo(
                Weather = WeatherResponse(),
                Forecast = requestedData // Initialize appropriately or pass actual data
            )
        } else {
            cityWeatherDetails?.Forecast = requestedData
        }
    }





    private fun updateWeatherUI(requestedData: WeatherResponse) {
        uiElements["date"]?.text = getCurrentDate()
        uiElements["cityName"]?.text = requestedData.name
        uiElements["currentTempValue"]?.text = requestedData.main?.temp.toString()
        uiElements["weatherValue"]?.text = requestedData.weather?.get(0)?.description?.capitalize()
        uiElements["humidityValue"]?.text = requestedData.main?.humidity.toString()
        uiElements["pressureValue"]?.text = requestedData.main?.pressure.toString()
        uiElements["tempMaxValue"]?.text = requestedData.main?.tempMax.toString()
        uiElements["tempMinValue"]?.text = requestedData.main?.tempMin.toString()
        uiElements["windSpeedValue"]?.text = requestedData.wind?.speed.toString()
        uiElements["cloudsValue"]?.text = requestedData.clouds?.all.toString()
        uiElements["sunriseValue"]?.text = formatTimestamp(requestedData.sys?.sunrise)
        uiElements["sunsetValue"]?.text = formatTimestamp(requestedData.sys?.sunset)

        if (cityWeatherDetails == null) {
            cityWeatherDetails = DatabasePojo(
                Weather = requestedData,
                Forecast = ForecastResponse() // Initialize appropriately or pass actual data
            )
        } else {
            cityWeatherDetails?.Weather = requestedData
        }
    }


    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

}
