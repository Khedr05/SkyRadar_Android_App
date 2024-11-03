package com.example.skyradar.favouritesLocations.view

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.skyradar.favouritesLocations.viewmodel.FavouritesLocationsViewModel
import com.example.skyradar.favouritesLocations.viewmodel.FavouritesLocationsViewModelFactory
import com.example.skyradar.home.view.DailyAdapter
import com.example.skyradar.home.view.HourlyAdapter
import com.example.skyradar.home.viewmodel.HomeViewModel
import com.example.skyradar.home.viewmodel.HomeFactory
import com.example.skyradar.model.DatabasePojo
import com.example.skyradar.model.ForecastResponse
import com.example.skyradar.model.RepositoryImpl
import com.example.skyradar.model.WeatherResponse
import com.example.skyradar.network.RemoteDataSourceImpl
import com.example.skyradar.network.ResponseStatus
import com.example.skyradar.network.RetrofitInstance
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FavouritesLocationsDetailsFragment : Fragment() {

    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var rvHourly: RecyclerView
    private lateinit var rvDaily: RecyclerView
    private lateinit var favViewModel: FavouritesLocationsViewModel
    private lateinit var homeViewModel: HomeViewModel
    private val uiElements: MutableMap<String, TextView> = mutableMapOf()
    private lateinit var rmFromFavouritesButton: ImageView

    private var selectedLocation: DatabasePojo? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_favourites_locations_details, container, false)

        // Retrieve the data
        selectedLocation = arguments?.getSerializable("selected_location") as? DatabasePojo

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)
        initializeUI(view)
        initializeViewModels()
        setupRecyclerView()
        fetchWeatherDataIfNeeded()
    }

    private fun fetchWeatherDataIfNeeded() {
        if (isNetworkAvailable()) {
            // Fetch new data from the network
            val latitude = selectedLocation?.Weather?.coord?.lat.toString()
            val longitude = selectedLocation?.Weather?.coord?.lon.toString()
            homeViewModel.fetchWeatherData(latitude, longitude, "metric", "en")
            homeViewModel.fetchForecastData(latitude, longitude, "metric", "en")
            observeViewModel();
            selectedLocation?.let { favViewModel.updateFavoriteLocation(it) }
            showDetails(selectedLocation)

        } else {
            // No network, show existing details
            showDetails(selectedLocation)
        }
    }


    private fun observeViewModel() {
        lifecycleScope.launch {
            homeViewModel.weatherData.collect { state -> handleWeatherResponse(state) }
        }
        lifecycleScope.launch {
            homeViewModel.forecastData.collect { state -> handleForecastResponse(state) }
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
        if (selectedLocation == null) {
            selectedLocation = DatabasePojo(
                Weather = WeatherResponse(),
                Forecast = requestedData // Initialize appropriately or pass actual data
            )
        } else {
            selectedLocation?.Forecast = requestedData
        }
    }



    private fun updateWeatherUI(requestedData: WeatherResponse) {
        if (selectedLocation == null) {
            selectedLocation = DatabasePojo(
                Weather = requestedData,
                Forecast = ForecastResponse() // Initialize appropriately or pass actual data
            )
        } else {
            selectedLocation?.Weather = requestedData
        }
    }



    private fun showDetails(location: DatabasePojo?) {
        uiElements["date"]?.text = getCurrentDate()
        uiElements["cityName"]?.text = location?.Weather?.name
        uiElements["currentTempValue"]?.text = location?.Weather?.main?.temp.toString()
        uiElements["weatherValue"]?.text = location?.Weather?.weather?.get(0)?.description?.capitalize()
        uiElements["humidityValue"]?.text = location?.Weather?.main?.humidity.toString()
        uiElements["pressureValue"]?.text = location?.Weather?.main?.pressure.toString()
        uiElements["tempMaxValue"]?.text = location?.Weather?.main?.tempMax.toString()
        uiElements["tempMinValue"]?.text = location?.Weather?.main?.tempMin.toString()
        uiElements["windSpeedValue"]?.text = location?.Weather?.wind?.speed.toString()
        uiElements["cloudsValue"]?.text = location?.Weather?.clouds?.all.toString()
        uiElements["sunriseValue"]?.text = formatTimestamp(location?.Weather?.sys?.sunrise)
        uiElements["sunsetValue"]?.text = formatTimestamp(location?.Weather?.sys?.sunset)
        location?.Forecast?.list?.let { dailyAdapter.submitWeatherList(it) }
        location?.Forecast?.list?.let { hourlyAdapter.submitTodayWeather(it) }
    }

    private fun initializeUI(view: View) {
        rmFromFavouritesButton = view.findViewById(R.id.btn_favourites)
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

        rmFromFavouritesButton.setOnClickListener {
            if (selectedLocation != null) {
                favViewModel.removeFavoriteLocation(selectedLocation!!)
                showSnackbar("Removed From Favorites")
            } else {
                showSnackbar("Error: No weather details available to remove.")
            }
        }
    }

    private fun initializeViewModels() {
        val factory = FavouritesLocationsViewModelFactory(
            RepositoryImpl(
                RemoteDataSourceImpl.getInstance(RetrofitInstance.retrofit),
                LocationLocalDataSourceImpl.getInstance(requireContext()),
                AlarmLocalDataSourceImpl.getInstance(requireContext()))
        )
        favViewModel = ViewModelProvider(this, factory).get(FavouritesLocationsViewModel::class.java)

        val homeFactory = HomeFactory(RepositoryImpl(
            RemoteDataSourceImpl.getInstance(RetrofitInstance.retrofit),
            LocationLocalDataSourceImpl.getInstance(requireContext()),
            AlarmLocalDataSourceImpl.getInstance(requireContext()))
        )
        homeViewModel = ViewModelProvider(this, homeFactory).get(HomeViewModel::class.java)
    }

    private fun setupRecyclerView() {
        hourlyAdapter = HourlyAdapter()
        rvHourly.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvHourly.adapter = hourlyAdapter
        dailyAdapter = DailyAdapter()
        rvDaily.layoutManager = LinearLayoutManager(requireContext())
        rvDaily.adapter = dailyAdapter
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }
}
