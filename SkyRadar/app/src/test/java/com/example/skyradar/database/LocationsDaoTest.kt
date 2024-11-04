package com.example.skyradar.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skyradar.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class LocationsDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var locationsDao: LocationsDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        locationsDao = database.productDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertLocation_andGetAllFavorites() = runBlockingTest {
        val location = DatabasePojo(
            id = 1,
            Weather = createWeatherResponse(),
            Forecast = createForecastResponse()
        )
        locationsDao.insertnewLocation(location)

        val result = locationsDao.getAllFavoritesLocation().first()

        assertThat(result, notNullValue())
        assertThat(result.size, `is`(1))
        assertThat(result[0].id, `is`(location.id))
    }

    @Test
    fun updateLocation_andVerifyUpdate() = runBlockingTest {
        val location = DatabasePojo(
            id = 1,
            Weather = createWeatherResponse(),
            Forecast = createForecastResponse()
        )
        locationsDao.insertnewLocation(location)

        val updatedLocation = location.copy(Weather = createUpdatedWeatherResponse())
        locationsDao.updateLocation(updatedLocation)

        val result = locationsDao.getAllFavoritesLocation().first()

        assertThat(result, notNullValue())
        assertThat(result.size, `is`(1))
        assertThat(result[0].Weather.name, `is`("Updated Location"))
    }

    @Test
    fun deleteLocation_andVerifyDeletion() = runBlockingTest {
        val location = DatabasePojo(
            id = 1,
            Weather = createWeatherResponse(),
            Forecast = createForecastResponse()
        )
        locationsDao.insertnewLocation(location)
        locationsDao.deleteCurrentLocation(location)

        val result = locationsDao.getAllFavoritesLocation().first()

        assertThat(result.isEmpty(), `is`(true))
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

    private fun createUpdatedWeatherResponse(): WeatherResponse {
        return createWeatherResponse().copy(name = "Updated Location")
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
