package com.example.skyradar.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skyradar.model.FakeRepository
import com.example.skyradar.model.ForecastResponse
import com.example.skyradar.model.WeatherResponse
import com.example.skyradar.network.ResponseStatus
import com.example.skyradar.home.viewmodel.HomeViewModel
import com.example.skyradar.model.City
import com.example.skyradar.model.Clouds
import com.example.skyradar.model.Coord
import com.example.skyradar.model.Main
import com.example.skyradar.model.Sys
import com.example.skyradar.model.Weather
import com.example.skyradar.model.WeatherList
import com.example.skyradar.model.Wind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var fakeRepository: FakeRepository
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Use the test dispatcher
        fakeRepository = FakeRepository()
        homeViewModel = HomeViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Clean up the main dispatcher
    }

    @Test
    fun fetchWeatherData_updatesWeatherData() = runTest(testDispatcher) {
        // Given
        val mockWeather = createWeatherResponse()
        fakeRepository.weatherResponse = mockWeather

        // When
        homeViewModel.fetchWeatherData("12.34", "56.78", "metric", "en")
        advanceUntilIdle() // Wait for all coroutines to finish

        // Then
        val weatherData = homeViewModel.weatherData.value
        assertThat(weatherData, equalTo(ResponseStatus.Success(mockWeather)))
    }

    @Test
    fun fetchForecastData_updatesForecastData() = runTest(testDispatcher) {
        // Given
        val mockForecast = createForecastResponse()
        fakeRepository.forecastResponse = mockForecast

        // When
        homeViewModel.fetchForecastData("12.34", "56.78", "metric", "en")
        advanceUntilIdle() // Wait for all coroutines to finish

        // Then
        val forecastData = homeViewModel.forecastData.value
        assertThat(forecastData, equalTo(ResponseStatus.Success(mockForecast)))
    }

    @Test
    fun fetchWeatherData_handlesError() = runTest(testDispatcher) {
        // Given
        fakeRepository.weatherResponse = null  // Simulate an error

        // When
        homeViewModel.fetchWeatherData("12.34", "56.78", "metric", "en")
        advanceUntilIdle() // Wait for all coroutines to finish

        // Then
        val weatherError = homeViewModel.weatherError.value
        assertThat(weatherError, equalTo("Error while fetching weather data: Simulated error"))
    }

    @Test
    fun fetchForecastData_handlesError() = runTest(testDispatcher) {
        // Given
        fakeRepository.forecastResponse = null  // Simulate an error

        // When
        homeViewModel.fetchForecastData("12.34", "56.78", "metric", "en")
        advanceUntilIdle() // Wait for all coroutines to finish

        // Then
        val forecastError = homeViewModel.forecastError.value
        assertThat(forecastError, equalTo("Error while fetching weather data: Simulated error"))
    }

    private fun createWeatherResponse(): WeatherResponse {
        return WeatherResponse(
            coord = Coord(lat = 12.34, lon = 56.78),
            weather = listOf(Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")),
            base = "stations",
            main = Main(
                temp = 300.0,
                feelsLike = 305.0,
                tempMin = 295.0,
                tempMax = 310.0,
                pressure = 1013,
                seaLevel = 1013,
                grndLevel = 1013,
                humidity = 40,
                tempKf = 0.0
            ),
            visibility = 10000,
            wind = Wind(speed = 3.5, deg = 150, gust = 5.5),
            clouds = Clouds(all = 1),
            dt = 1605182400,
            sys = Sys(sunrise = 1605155400, sunset = 1605193200),
            timezone = 3600,
            id = 1,
            name = "Test Location",
            cod = 200
        )
    }

    private fun createForecastResponse(): ForecastResponse {
        val forecastList = listOf(
            WeatherList(
                dt = 1605182400,
                main = Main(
                    temp = 300.0,
                    feelsLike = 305.0,
                    tempMin = 295.0,
                    tempMax = 310.0,
                    pressure = 1013,
                    seaLevel = 1013,
                    grndLevel = 1013,
                    humidity = 40,
                    tempKf = 0.0
                ),
                weather = listOf(Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")),
                clouds = Clouds(all = 1),
                wind = Wind(speed = 3.5, deg = 150, gust = 5.5),
                visibility = 10000,
                pop = 0.2,
                sys = Sys(sunrise = 1605155400, sunset = 1605193200),
                dtTxt = "2020-12-12 12:00:00"
            )
        )

        return ForecastResponse(
            list = forecastList,
            city = City(
                id = 1,
                name = "Test City",
                coord = Coord(lat = 12.34, lon = 56.78),
                country = "Test Country",
                population = 100000,
                timezone = 3600,
                sunrise = 1605155400,
                sunset = 1605193200
            )
        )
    }
}
