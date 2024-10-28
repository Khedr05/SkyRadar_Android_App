package com.example.skyradar.map.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.R
import com.example.skyradar.database.LocationLocalDataSourceImpl
import com.example.skyradar.model.ForecastResponse
import com.example.skyradar.model.RepositoryImpl
import com.example.skyradar.model.WeatherResponse
import com.example.skyradar.network.RemoteDataSourceImpl
import com.example.skyradar.network.ResponseStatus
import com.example.skyradar.network.RetrofitInstance
import com.example.skyradar.home.viewmodel.HomeFactory
import com.example.skyradar.home.viewmodel.HomeViewModel
import com.example.skyradar.home.view.HomeAdapter
import com.example.skyradar.model.DatabasePojo
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MapWeatherDetailsFragment : Fragment(){

    private lateinit var homeAdapter: HomeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: HomeViewModel
    private val uiElements: MutableMap<String, TextView> = mutableMapOf()
    private lateinit var addToFavouritesButton: Button

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
        recyclerView = view.findViewById(R.id.recyclerView)
        addToFavouritesButton = view.findViewById(R.id.buttonAddToFavourite)
        uiElements["cityName"] = view.findViewById(R.id.textView)
        uiElements["currentTempValue"] = view.findViewById(R.id.currentTempValue)
        uiElements["weatherValue"] = view.findViewById(R.id.weatherValue)
        uiElements["humidityValue"] = view.findViewById(R.id.humidityValue)
        uiElements["pressureValue"] = view.findViewById(R.id.pressureValue)
        uiElements["tempMaxValue"] = view.findViewById(R.id.tempMaxValue)
        uiElements["tempMinValue"] = view.findViewById(R.id.tempMinValue)
        uiElements["windSpeedValue"] = view.findViewById(R.id.windSpeedValue)
        uiElements["cloudsValue"] = view.findViewById(R.id.cloudsValue)

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
                LocationLocalDataSourceImpl.getInstance(requireContext()))
        )
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
    }

    private fun setupRecyclerView() {
        homeAdapter = HomeAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = homeAdapter
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
        homeAdapter.submitList(requestedData.list)
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
        uiElements["cityName"]?.text = requestedData.name ?: "N/A"
        uiElements["currentTempValue"]?.text = "${requestedData.main?.temp ?: "N/A"} °C"
        uiElements["weatherValue"]?.text = requestedData.weather?.get(0)?.description?.capitalize() ?: "N/A"
        uiElements["humidityValue"]?.text = "${requestedData.main?.humidity ?: "N/A"}%"
        uiElements["pressureValue"]?.text = "${requestedData.main?.pressure ?: "N/A"} hPa"
        uiElements["tempMaxValue"]?.text = "${requestedData.main?.tempMax ?: "N/A"} °C"
        uiElements["tempMinValue"]?.text = "${requestedData.main?.tempMin ?: "N/A"} °C"
        uiElements["windSpeedValue"]?.text = "${requestedData.wind?.speed ?: "N/A"} m/s"
        uiElements["cloudsValue"]?.text = "${requestedData.clouds?.all ?: "N/A"} %"

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
