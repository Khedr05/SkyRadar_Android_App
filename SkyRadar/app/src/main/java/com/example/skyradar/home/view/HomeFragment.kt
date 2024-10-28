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
import com.example.skyradar.database.LocationLocalDataSourceImpl

class HomeFragment : Fragment() {

    private lateinit var homeAdapter: HomeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: HomeViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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
        setupRecyclerView()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        observeViewModel()
        checkLocationEnabled()
    }

    override fun onResume() {
        super.onResume()
        checkLocationEnabled()
    }

    private fun initializeUI(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        uiElements["cityName"] = view.findViewById(R.id.textView)
        uiElements["currentTempValue"] = view.findViewById(R.id.currentTempValue)
        uiElements["weatherValue"] = view.findViewById(R.id.weatherValue)
        uiElements["humidityValue"] = view.findViewById(R.id.humidityValue)
        uiElements["pressureValue"] = view.findViewById(R.id.pressureValue)
        uiElements["tempMaxValue"] = view.findViewById(R.id.tempMaxValue)
        uiElements["tempMinValue"] = view.findViewById(R.id.tempMinValue)
        uiElements["windSpeedValue"] = view.findViewById(R.id.windSpeedValue)
        uiElements["cloudsValue"] = view.findViewById(R.id.cloudsValue)
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
        viewModel.fetchForecastData(location.latitude.toString(), location.longitude.toString(), "metric", "en")
        viewModel.fetchWeatherData(location.latitude.toString(), location.longitude.toString(), "metric", "en")
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
                    is ForecastResponse -> homeAdapter.submitList(requestedData.list)
                }
            }
            is ResponseStatus.Failure -> showSnackbar("Error: ${state.errorMessage}")
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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
