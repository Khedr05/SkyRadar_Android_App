package com.example.skyradar.network

import com.example.skyradar.model.ForecastResponse
import com.example.skyradar.model.WeatherResponse
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response

class FakeRemoteDataSource : RemoteDataSource {

    var weather: WeatherResponse? = null
    var forecast: ForecastResponse? = null
    var shouldReturnError: Boolean = false

    override suspend fun getForecastData(
        latitude: String,
        longitude: String,
        units: String,
        lang: String
    ): Response<ForecastResponse> {
        return if (shouldReturnError) {
            Response.error(
                500,
                ResponseBody.create(
                    MediaType.parse("application/json"),
                    "{\"error\":\"Failed to fetch forecast data\"}"
                )
            )
        } else {
            Response.success(forecast)
        }
    }

    override suspend fun getForecastDataByCityName(
        cityName: String,
        units: String,
        lang: String
    ): Response<ForecastResponse> {
        return if (shouldReturnError) {
            Response.error(
                500,
                ResponseBody.create(
                    MediaType.parse("application/json"),
                    "{\"error\":\"Failed to fetch forecast data by city name\"}"
                )
            )
        } else {
            Response.success(forecast)
        }
    }

    override suspend fun getWeatherData(
        latitude: String,
        longitude: String,
        units: String,
        lang: String
    ): Response<WeatherResponse> {
        return if (shouldReturnError) {
            Response.error(
                500,
                ResponseBody.create(
                    MediaType.parse("application/json"),
                    "{\"error\":\"Failed to fetch weather data\"}"
                )
            )
        } else {
            Response.success(weather)
        }
    }

    override suspend fun getWeatherDataByCityName(
        cityName: String,
        units: String,
        lang: String
    ): Response<WeatherResponse> {
        return if (shouldReturnError) {
            Response.error(
                500,
                ResponseBody.create(
                    MediaType.parse("application/json"),
                    "{\"error\":\"Failed to fetch weather data by city name\"}"
                )
            )
        } else {
            Response.success(weather)
        }
    }
}
