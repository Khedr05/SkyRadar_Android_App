package com.example.skyradar.favouritesLocations

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.skyradar.favouritesLocations.viewmodel.FavouritesLocationsViewModel
import com.example.skyradar.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class FavouritesLocationsViewModelTest {

    private lateinit var viewModel: FavouritesLocationsViewModel
    private lateinit var fakeRepository: FakeRepository
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeRepository()
        viewModel = FavouritesLocationsViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun addFavoriteLocation_updatesFavoriteLocations() = runTest(testDispatcher) {
        // Given
        val location = DatabasePojo(id = 1 ,Weather = createWeatherResponse(), Forecast = createForecastResponse())

        // When
        viewModel.updateFavoriteLocation(location)
        advanceUntilIdle()

        // Then
        val favoriteLocations = viewModel.favoriteLocations.value
        assertEquals(1, favoriteLocations.size)
        assertThat(favoriteLocations[0], `is`(location))
    }

    @Test
    fun removeFavoriteLocation_updatesFavoriteLocations() = runTest(testDispatcher) {
        // Given
        val location = DatabasePojo(id = 1, Weather = createWeatherResponse(), Forecast = createForecastResponse())
        viewModel.updateFavoriteLocation(location)
        advanceUntilIdle()
        assertEquals(1, viewModel.favoriteLocations.value.size)

        // When
        viewModel.removeFavoriteLocation(location)
        advanceUntilIdle()

        // Then
        val favoriteLocations = viewModel.favoriteLocations.value
        assertEquals(0, favoriteLocations.size)
    }

    @Test
    fun updateFavoriteLocation_updatesFavoriteLocations() = runTest(testDispatcher) {
        // Given
        val location = DatabasePojo(id = 1, Weather = createWeatherResponse(), Forecast = createForecastResponse())
        viewModel.updateFavoriteLocation(location)
        advanceUntilIdle()
        assertEquals(1, viewModel.favoriteLocations.value.size)

        val updatedLocation = location.copy(Weather = createWeatherResponse(), Forecast = createForecastResponse())

        // When
        viewModel.updateFavoriteLocation(updatedLocation)
        advanceUntilIdle()

        // Then
        val favoriteLocations = viewModel.favoriteLocations.value
        assertEquals(1, favoriteLocations.size)
        assertThat(favoriteLocations[0], `is`(updatedLocation))
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
