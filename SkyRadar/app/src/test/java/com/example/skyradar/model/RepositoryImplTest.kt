package com.example.skyradar.model

import android.content.SharedPreferences
import com.example.skyradar.database.FakeAlarmLocalDataSource
import com.example.skyradar.database.FakeLocalDataSource
import com.example.skyradar.network.FakeRemoteDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertFailsWith

class RepositoryImplTest {

    private lateinit var repository: RepositoryImpl
    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    private lateinit var fakeLocalDataSource: FakeLocalDataSource
    private lateinit var fakeAlarmLocalDataSource: FakeAlarmLocalDataSource
    private lateinit var mockSharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        // Mocking SharedPreferences
        mockSharedPreferences = mock()
        whenever(mockSharedPreferences.getString(any(), any())).thenReturn("Metric")

        // Initialize fake data sources
        fakeRemoteDataSource = FakeRemoteDataSource()
        fakeLocalDataSource = FakeLocalDataSource()
        fakeAlarmLocalDataSource = FakeAlarmLocalDataSource()

        // Initialize repository with mocked shared preferences
        repository = RepositoryImpl(
            remoteDataSource = fakeRemoteDataSource,
            localDataSource = fakeLocalDataSource,
            alarmLocalDataSource = fakeAlarmLocalDataSource,
            sharedPreferences = mockSharedPreferences
        )
    }

    @Test
    fun getWeatherData_RemoteSuccessful_ReturnsWeatherData() = runTest {
        // Given
        val expectedWeather = WeatherResponse(/* Initialize with test data */)
        fakeRemoteDataSource.weather = expectedWeather

        // When
        val result = repository.getWeatherData("12.34", "56.78", "metric", "en").first()

        // Then
        assertThat(result, `is`(expectedWeather))
    }

    @Test
    fun getWeatherData_RemoteFailed_ThrowsException() = runTest {
        // Given
        fakeRemoteDataSource.shouldReturnError = true

        // When
        val exception = assertFailsWith<Exception> {
            repository.getWeatherData("12.34", "56.78", "metric", "en").first()
        }

        // Then
        assertTrue(exception.message?.contains("Failed to fetch weather data") == true)
    }

    @Test
    fun getForecastData_RemoteSuccessful_ReturnsForecastData() = runTest {
        // Given
        val expectedForecast = ForecastResponse(/* Initialize with test data */)
        fakeRemoteDataSource.forecast = expectedForecast

        // When
        val result = repository.getForecastData("12.34", "56.78", "metric", "en").first()

        // Then
        assertThat(result, `is`(expectedForecast))
    }

    @Test
    fun getForecastData_RemoteFailed_ThrowsException() = runTest {
        // Given
        fakeRemoteDataSource.shouldReturnError = true

        // When
        val exception = assertFailsWith<Exception> {
            repository.getForecastData("12.34", "56.78", "metric", "en").first()
        }

        // Then
        assertTrue(exception.message?.contains("Failed to fetch forecast data") == true)
    }


    @Test
    fun insertAlarm_AddsAlarmSuccessfully() = runTest {
        // Given
        val alarm = Alarm(id = 1, timeInMillis = 28800000L) // 08:00 in milliseconds

        // When
        repository.insertAlarm(alarm)

        // Then
        val alarms = repository.getAlarms().first()
        assertThat(alarms.contains(alarm), `is`(true))
    }

    @Test
    fun deleteAlarm_DeletesAlarmSuccessfully() = runTest {
        // Given
        val alarm = Alarm(id = 1, timeInMillis = 28800000L) // 08:00 in milliseconds
        repository.insertAlarm(alarm)

        // When
        repository.deleteAlarm(alarm)

        // Then
        val alarms = repository.getAlarms().first()
        assertThat(alarms.contains(alarm), `is`(false))
    }
}
