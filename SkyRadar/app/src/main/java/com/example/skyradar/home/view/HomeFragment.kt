package com.example.skyradar.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.R
import com.example.skyradar.model.ForecastResponse
import com.example.skyradar.model.RepositoryImpl
import com.example.skyradar.model.WeatherResponse
import com.example.skyradar.network.RemoteDataSourceImpl
import com.example.skyradar.network.ResponseStatus
import com.example.skyradar.network.RetrofitInstance
import com.example.skyradar.home.viewmodel.HomeFactory
import com.example.skyradar.home.viewmodel.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var homeAdapter: HomeAdapter
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
    private lateinit var viewModel: HomeViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView)
        cityNameView = view.findViewById(R.id.textView)
        currentTempValue = view.findViewById(R.id.currentTempValue)
        weatherValue = view.findViewById(R.id.weatherValue)
        humidityValue = view.findViewById(R.id.humidityValue)
        pressureValue = view.findViewById(R.id.pressureValue)
        tempMaxValue = view.findViewById(R.id.tempMaxValue)
        tempMinValue = view.findViewById(R.id.tempMinValue)
        windSpeedValue = view.findViewById(R.id.windSpeedValue)
        cloudsValue = view.findViewById(R.id.cloudsValue)

        homeAdapter = HomeAdapter()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = homeAdapter

        val factory = HomeFactory(
            RepositoryImpl(RemoteDataSourceImpl.getInstance(RetrofitInstance.retrofit))
        )

        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
        } else {
            fetchLocationAndInitializeWeatherData()
        }

        //observeViewModel()

    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationAndInitializeWeatherData()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission is required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocationAndInitializeWeatherData() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                initializeWeatherData(location.latitude, location.longitude)
            } else {
                Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializeWeatherData(lat: Double, lon: Double) {
        observeViewModel()
        viewModel.fetchForecastData(lat.toString(), lon.toString(), "metric", "en")
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
                                homeAdapter.submitList(requestedData.list)
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
                        Toast.makeText(requireContext(), "Error: ${state.errorMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
