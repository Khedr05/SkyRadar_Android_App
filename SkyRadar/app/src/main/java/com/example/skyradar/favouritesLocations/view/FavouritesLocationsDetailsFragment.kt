// FavouritesLocationsDetailsFragment.kt
package com.example.skyradar.favouritesLocations.view

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
import com.example.skyradar.home.viewmodel.HomeFactory
import com.example.skyradar.home.viewmodel.HomeViewModel
import com.example.skyradar.model.DatabasePojo
import com.example.skyradar.model.RepositoryImpl
import com.example.skyradar.network.RemoteDataSourceImpl
import com.example.skyradar.network.RetrofitInstance
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FavouritesLocationsDetailsFragment : Fragment() {

    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var rvHourly: RecyclerView
    private lateinit var rvDaily: RecyclerView
    private lateinit var favViewModel: FavouritesLocationsViewModel
    private val uiElements: MutableMap<String, TextView> = mutableMapOf()
    private lateinit var rmFromFavouritesButton: ImageView

    private var selectedLocation : DatabasePojo? = null
    private var cityWeatherDetails: DatabasePojo? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_favourites_locations_details, container, false)

        // Retrieve the data
         selectedLocation = arguments?.getSerializable("selected_location") as? DatabasePojo

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI(view)
        initializeViewModel()
        setupRecyclerView()
        showDetails()
        //observeViewModel()

    }

    private fun showDetails() {
        uiElements["date"]?.text = getCurrentDate()
        uiElements["cityName"]?.text = selectedLocation?.Weather?.name
        uiElements["currentTempValue"]?.text = selectedLocation?.Weather?.main?.temp.toString()
        uiElements["weatherValue"]?.text = selectedLocation?.Weather?.weather?.get(0)?.description?.capitalize()
        uiElements["humidityValue"]?.text = selectedLocation?.Weather?.main?.humidity.toString()
        uiElements["pressureValue"]?.text = selectedLocation?.Weather?.main?.pressure.toString()
        uiElements["tempMaxValue"]?.text = selectedLocation?.Weather?.main?.tempMax.toString()
        uiElements["tempMinValue"]?.text = selectedLocation?.Weather?.main?.tempMin.toString()
        uiElements["windSpeedValue"]?.text = selectedLocation?.Weather?.wind?.speed.toString()
        uiElements["cloudsValue"]?.text = selectedLocation?.Weather?.clouds?.all.toString()
        uiElements["sunriseValue"]?.text = formatTimestamp(selectedLocation?.Weather?.sys?.sunrise)
        uiElements["sunsetValue"]?.text = formatTimestamp(selectedLocation?.Weather?.sys?.sunset)
        selectedLocation?.Forecast?.list?.let { dailyAdapter.submitWeatherList(it) }
        selectedLocation?.Forecast?.list?.let { hourlyAdapter.submitTodayWeather(it) }
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
            // Handle the button click event
            if (selectedLocation != null) {
                // Save cityWeatherDetails to favorites or handle the favorite action
                favViewModel.removeFavoriteLocation(selectedLocation!!)
                showSnackbar("Removed From Favorites")
            } else {
                showSnackbar("Error: No weather details available to remove.")
            }
        }
    }

    private fun initializeViewModel() {
        val factory = FavouritesLocationsViewModelFactory(
            RepositoryImpl(
                RemoteDataSourceImpl.getInstance(RetrofitInstance.retrofit),
                LocationLocalDataSourceImpl.getInstance(requireContext()),
                AlarmLocalDataSourceImpl.getInstance(requireContext()))
        )
        favViewModel = ViewModelProvider(this, factory).get(FavouritesLocationsViewModel::class.java)
    }

    private fun setupRecyclerView() {
        hourlyAdapter = HourlyAdapter()
        rvHourly.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvHourly.adapter = hourlyAdapter
        dailyAdapter = DailyAdapter()
        rvDaily.layoutManager = LinearLayoutManager(requireContext())
        rvDaily.adapter = dailyAdapter
    }



    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }
}
