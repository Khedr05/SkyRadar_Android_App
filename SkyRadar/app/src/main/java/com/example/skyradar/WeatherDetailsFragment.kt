package com.example.skyradar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.skyradar.model.ForecastResponse
import com.example.skyradar.model.RepositoryImpl
import com.example.skyradar.model.WeatherResponse
import com.example.skyradar.network.RemoteDataSourceImpl
import com.example.skyradar.network.ResponseStatus
import com.example.skyradar.network.RetrofitInstance
import com.example.skyradar.home.viewmodel.HomeFactory
import com.example.skyradar.home.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class WeatherDetailsFragment : DialogFragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var cityNameView: TextView
    private lateinit var currentTempValue: TextView
    private lateinit var weatherValue: TextView
    private lateinit var humidityValue: TextView
    private lateinit var pressureValue: TextView
    private lateinit var tempMaxValue: TextView
    private lateinit var tempMinValue: TextView
    private lateinit var windSpeedValue: TextView
    private lateinit var cloudsValue: TextView

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0


    override fun onStart() {
        super.onStart()

        isCancelable = true

        dialog?.setOnShowListener {
            val dialogView = requireDialog().window?.decorView
            dialogView?.setOnClickListener {
                dismiss()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latitude = it.getDouble("latitude", 0.0)
            longitude = it.getDouble("longitude", 0.0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather_details, container, false)

        // Initialize views
       progressBar = view.findViewById(R.id.progressBar)
       cityNameView = view.findViewById(R.id.textView)
        currentTempValue = view.findViewById(R.id.currentTempValue)
        weatherValue = view.findViewById(R.id.weatherValue)
        humidityValue = view.findViewById(R.id.humidityValue)
        pressureValue = view.findViewById(R.id.pressureValue)
        tempMaxValue = view.findViewById(R.id.tempMaxValue)
        tempMinValue = view.findViewById(R.id.tempMinValue)
        windSpeedValue = view.findViewById(R.id.windSpeedValue)
        cloudsValue = view.findViewById(R.id.cloudsValue)

        // Initialize ViewModel
        val factory = HomeFactory(
            RepositoryImpl(RemoteDataSourceImpl.getInstance(RetrofitInstance.retrofit))
        )
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        fetchWeatherData()

        return view
    }

    private fun fetchWeatherData() {
        lifecycleScope.launch {
            viewModel.fetchForecastData(latitude.toString(), longitude.toString(), "metric", "en")
            observeViewModel()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.forecastData.collect { state ->
                when (state) {
                    is ResponseStatus.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        hideWeatherViews()
                    }
                    is ResponseStatus.Success<*> -> {
                        progressBar.visibility = View.GONE
                        state.requestedData?.let { data ->
                            when (data) {
                                is ForecastResponse -> {
                                    showWeatherViews()
                                    updateUI(data)
                                }
                                is WeatherResponse -> {
                                    showWeatherViews()
                                    updateUI(data)
                                }
                            }
                        }
                    }
                    is ResponseStatus.Failure -> {
                        progressBar.visibility = View.GONE
                        hideWeatherViews()
                        Toast.makeText(requireContext(), "Error: ${state.errorMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateUI(data: ForecastResponse) {
        cityNameView.text = data.city.name
        currentTempValue.text = "${data.list[0].main.temp} °C"
        weatherValue.text = data.list[0].weather[0].description.capitalize()
        humidityValue.text = "${data.list[0].main.humidity}%"
        pressureValue.text = "${data.list[0].main.pressure} hPa"
        tempMaxValue.text = "${data.list[0].main.tempMax} °C"
        tempMinValue.text = "${data.list[0].main.tempMin} °C"
        windSpeedValue.text = "${data.list[0].wind.speed} m/s"
        cloudsValue.text = "${data.list[0].clouds.all} %"
    }

    private fun updateUI(data: WeatherResponse) {
       cityNameView.text = data.name
        currentTempValue.text = "${data.main.temp} °C"
        weatherValue.text = data.weather[0].description.capitalize()
        humidityValue.text = "${data.main.humidity}%"
        pressureValue.text = "${data.main.pressure} hPa"
        tempMaxValue.text = "${data.main.tempMax} °C"
        tempMinValue.text = "${data.main.tempMin} °C"
        windSpeedValue.text = "${data.wind.speed} m/s"
        cloudsValue.text = "${data.clouds.all} %"
    }

    private fun hideWeatherViews() {
        cityNameView.visibility = View.GONE
        currentTempValue.visibility = View.GONE
        weatherValue.visibility = View.GONE
        humidityValue.visibility = View.GONE
        pressureValue.visibility = View.GONE
        tempMaxValue.visibility = View.GONE
        tempMinValue.visibility = View.GONE
        windSpeedValue.visibility = View.GONE
        cloudsValue.visibility = View.GONE
    }

    private fun showWeatherViews() {
        cityNameView.visibility = View.VISIBLE
        currentTempValue.visibility = View.VISIBLE
        weatherValue.visibility = View.VISIBLE
        humidityValue.visibility = View.VISIBLE
        pressureValue.visibility = View.VISIBLE
        tempMaxValue.visibility = View.VISIBLE
        tempMinValue.visibility = View.VISIBLE
        windSpeedValue.visibility = View.VISIBLE
        cloudsValue.visibility = View.VISIBLE
    }
}
