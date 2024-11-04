//package com.example.skyradar.database
//
//import com.example.skyradar.model.City
//import com.example.skyradar.model.Clouds
//import com.example.skyradar.model.Coord
//import com.example.skyradar.model.DatabasePojo
//import com.example.skyradar.model.ForecastResponse
//import com.example.skyradar.model.Main
//import com.example.skyradar.model.Sys
//import com.example.skyradar.model.Weather
//import com.example.skyradar.model.WeatherList
//import com.example.skyradar.model.WeatherResponse
//import com.example.skyradar.model.Wind
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import android.content.Context
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.room.Room
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.test.runTest
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.Matchers.`is`
//import org.junit.After
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//
//@RunWith(AndroidJUnit4::class)
//@ExperimentalCoroutinesApi
//class LocationLocalDataSourceImplTest {
//
//    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    private lateinit var database: AppDatabase
//    private lateinit var locationsDao: LocationsDao
//    private lateinit var locationLocalDataSource: LocationLocalDataSourceImpl
//
//    @Before
//    fun setup() {
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        database = Room.inMemoryDatabaseBuilder(
//            context,
//            AppDatabase::class.java
//        ).allowMainThreadQueries().build()
//
//        locationsDao = database.productDao()
//        locationLocalDataSource = LocationLocalDataSourceImpl(context)
//    }
//
//    @After
//    fun tearDown() {
//        database.close()
//    }
//
//
//
//    @Test
//    fun addFavorite_insertsLocation() = runTest {
//        val location = DatabasePojo(
//            id = 1,
//            Weather = createWeatherResponse(),
//            Forecast = createForecastResponse()
//        )
//
//        locationLocalDataSource.addFavorite(location)
//
//        val favorites = locationLocalDataSource.getFavoriteLocations().first()
//
//        assertThat(favorites.size, `is`(1))
//        assertThat(favorites[0].id, `is`(location.id))
//        assertThat(favorites[0].Weather.name, `is`(location.Weather.name))
//    }
//
//    @Test
//    fun removeFavorite_deletesLocation() = runTest {
//        val location = DatabasePojo(
//            id = 1,
//            Weather = createWeatherResponse(),
//            Forecast = createForecastResponse()
//        )
//
//        locationLocalDataSource.addFavorite(location)
//
//        locationLocalDataSource.removeFavorite(location)
//
//        val favorites = locationLocalDataSource.getFavoriteLocations().first()
//
//        assertThat(favorites.size, `is`(0))
//    }
//
//    @Test
//    fun updateFavorite_updatesLocation() = runBlockingTest {
//        val location = DatabasePojo(
//            id = 1,
//            Weather = createWeatherResponse(),
//            Forecast = createForecastResponse()
//        )
//
//        locationLocalDataSource.addFavorite(location)
//
//        val updatedLocation = location.copy(Weather = createWeatherResponse().copy(name = "Updated Location"))
//        locationLocalDataSource.updateFavorite(updatedLocation)
//
//        val favorites = locationLocalDataSource.getFavoriteLocations().first()
//
//        assertThat(favorites.size, `is`(1))
//        assertThat(favorites[0].Weather.name, `is`("Updated Location"))
//    }
//
//    @Test
//    fun getFavoriteLocations_returnsAllFavorites() = runTest {
//        val location1 = DatabasePojo(
//            id = 1,
//            Weather = createWeatherResponse(),
//            Forecast = createForecastResponse()
//        )
//        val location2 = DatabasePojo(
//            id = 2,
//            Weather = createWeatherResponse(),
//            Forecast = createForecastResponse()
//        )
//
//        locationLocalDataSource.addFavorite(location1)
//        locationLocalDataSource.addFavorite(location2)
//
//        val favorites = locationLocalDataSource.getFavoriteLocations().first()
//
//        assertThat(favorites.size, `is`(2))
//        assertThat(favorites[0].id, `is`(1))
//        assertThat(favorites[1].id, `is`(2))
//    }
//
//
//
//    private fun createWeatherResponse(): WeatherResponse {
//        return WeatherResponse(
//            coord = Coord(lat = 12.34, lon = 56.78),
//            weather = listOf(Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")),
//            base = "stations",
//            main = Main(
//                temp = 300.0,
//                feelsLike = 305.0,
//                tempMin = 295.0,
//                tempMax = 310.0,
//                pressure = 1013,
//                seaLevel = 1013,
//                grndLevel = 1013,
//                humidity = 40,
//                tempKf = 0.0
//            ),
//            visibility = 10000,
//            wind = Wind(speed = 3.5, deg = 150, gust = 5.5),
//            clouds = Clouds(all = 1),
//            dt = 1605182400,
//            sys = Sys(sunrise = 1605155400, sunset = 1605193200),
//            timezone = 3600,
//            id = 1,
//            name = "Test Location",
//            cod = 200
//        )
//    }
//
//
//    private fun createForecastResponse(): ForecastResponse {
//        val forecastList = listOf(
//            WeatherList(
//                dt = 1605182400,
//                main = Main(
//                    temp = 300.0,
//                    feelsLike = 305.0,
//                    tempMin = 295.0,
//                    tempMax = 310.0,
//                    pressure = 1013,
//                    seaLevel = 1013,
//                    grndLevel = 1013,
//                    humidity = 40,
//                    tempKf = 0.0
//                ),
//                weather = listOf(Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")),
//                clouds = Clouds(all = 1),
//                wind = Wind(speed = 3.5, deg = 150, gust = 5.5),
//                visibility = 10000,
//                pop = 0.2,
//                sys = Sys(sunrise = 1605155400, sunset = 1605193200),
//                dtTxt = "2020-12-12 12:00:00"
//            )
//        )
//
//        return ForecastResponse(
//            list = forecastList,
//            city = City(
//                id = 1,
//                name = "Test City",
//                coord = Coord(lat = 12.34, lon = 56.78),
//                country = "Test Country",
//                population = 100000,
//                timezone = 3600,
//                sunrise = 1605155400,
//                sunset = 1605193200
//            )
//        )
//    }
//}
//
//
