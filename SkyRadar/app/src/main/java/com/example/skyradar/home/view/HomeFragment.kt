package com.example.skyradar.home.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
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
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import com.example.skyradar.database.AlarmLocalDataSourceImpl
import com.example.skyradar.database.LocationLocalDataSourceImpl
import java.util.Locale
import android.icu.text.SimpleDateFormat
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.ProgressBar
import androidx.fragment.app.FragmentTransaction
import com.example.skyradar.Helpers.formatTimestamp
import com.example.skyradar.Helpers.getCurrentDate
import com.example.skyradar.Helpers.getMeasurementString
import com.example.skyradar.NetworkIssueFragment
import java.util.Date

class HomeFragment : Fragment() {

    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var rvHourly: RecyclerView
    private lateinit var rvDaily: RecyclerView
    private lateinit var viewModel: HomeViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var progressBarHourly: ProgressBar
    private lateinit var progressBarDaily: ProgressBar

    private val uiElements: MutableMap<String, TextView> = mutableMapOf()

    private var isAlertDialogShown = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI(view)
        initializeViewModel()
        initializeSettings()
        setupRecyclerView()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (!isNetworkAvailable()) {
            // Replace HomeFragment with NetworkIssueFragment
            val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, NetworkIssueFragment()) // Replace with your fragment container ID
            transaction.addToBackStack(null) // Optional: add to back stack to allow back navigation
            transaction.commit()
        } else {
            observeViewModel()
            checkLocationEnabled()
        }
    }

    private fun initializeSettings() {
        val unit: String? = viewModel.getUnit()
        val language: String? = viewModel.getLanguage()

        if(unit == null){
            viewModel.setUnit("metric")
        }else{/* Do Nothing */}

        if(language == null || language == "Default Mobile Language"){

            val deviceLanguage = Locale.getDefault().language
            Log.i("ello8a", "Device Language: $deviceLanguage")
            viewModel.setLanguage(deviceLanguage)
        }else{/* Do Nothing */}
    }

    override fun onResume() {
        super.onResume()
        checkLocationEnabled()
    }

    private fun initializeUI(view: View) {
        rvHourly = view.findViewById(R.id.rv_hourly_degrees)
        rvDaily = view.findViewById(R.id.rv_detailed_days)
        progressBarHourly = view.findViewById(R.id.progressBarHourly)
        progressBarDaily = view.findViewById(R.id.progressBarDaily)
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

    private fun checkLocationEnabled() {
        val locationManager = requireContext().getSystemService(LocationManager::class.java)
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            if (!isAlertDialogShown) {
                showLocationEnableAlert()
                isAlertDialogShown = true
            }
        } else {
            isAlertDialogShown = false
            checkLocationPermissionAndRequestLocation()
        }
    }

    private fun showLocationEnableAlert() {
        AlertDialog.Builder(requireContext())
            .setTitle("Enable Location Services")
            .setMessage("This app requires location services to provide weather updates. Please enable location services in your settings.")
            .setPositiveButton("Enable") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                isAlertDialogShown = false
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                isAlertDialogShown = false
            }
            .show()
    }

    private fun checkLocationPermissionAndRequestLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestCurrentLocation()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun requestCurrentLocation() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    fusedLocationClient.removeLocationUpdates(this)
                    val location = locationResult.lastLocation
                    if (location != null) {
                        fetchWeatherData(location)
                    } else {
                        showSnackbar("Failed to get location")
                    }
                }
            }, null)
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun fetchWeatherData(location: android.location.Location) {

        var lang : String? = viewModel.getLanguage()
        var unit : String? = viewModel.getUnit()

        viewModel.fetchWeatherData(location.latitude.toString(), location.longitude.toString(), unit.toString(), lang.toString())
        viewModel.fetchForecastData(location.latitude.toString(), location.longitude.toString(), unit.toString(), lang.toString())
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
                    is ForecastResponse -> {
                        requestedData.list?.let { dailyAdapter.submitWeatherList(it) }
                        requestedData.list?.let { hourlyAdapter.submitTodayWeather(it) }
                    }
                }
            }
            is ResponseStatus.Failure -> showSnackbar("Error: ${state.errorMessage}")
        }
    }


    private fun updateWeatherUI(requestedData: WeatherResponse) {

        uiElements["date"]?.text = getCurrentDate()
        uiElements["cityName"]?.text = requestedData.name
        uiElements["currentTempValue"]?.text = requestedData.main?.temp.toString() + getMeasurementString(false, lang = viewModel.getLanguage().toString(), unit = viewModel.getUnit().toString())
        uiElements["weatherValue"]?.text = requestedData.weather?.get(0)?.description?.capitalize()
        uiElements["humidityValue"]?.text = requestedData.main?.humidity.toString()
        uiElements["pressureValue"]?.text = requestedData.main?.pressure.toString()
        uiElements["tempMaxValue"]?.text = requestedData.main?.tempMax.toString()
        uiElements["tempMinValue"]?.text = requestedData.main?.tempMin.toString()
        uiElements["windSpeedValue"]?.text = requestedData.wind?.speed.toString() + getMeasurementString(true, lang = viewModel.getLanguage().toString(), unit = viewModel.getUnit().toString())
        uiElements["cloudsValue"]?.text = requestedData.clouds?.all.toString()
        uiElements["sunriseValue"]?.text = formatTimestamp(requestedData.sys?.sunrise)
        uiElements["sunsetValue"]?.text = formatTimestamp(requestedData.sys?.sunset)

    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestCurrentLocation()
        } else {
            showSnackbar("Location permission is required to get your current weather.")
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork?.let {
                connectivityManager.getNetworkCapabilities(it)
            }
            return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
