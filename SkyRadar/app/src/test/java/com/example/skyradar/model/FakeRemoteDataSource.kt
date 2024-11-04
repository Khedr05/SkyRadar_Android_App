//package com.example.skyradar.model
//
//import com.example.skyradar.network.RemoteDataSource
//import com.example.skyradar.network.RemoteDataSourceImpl
//import okhttp3.MediaType
//import okhttp3.ResponseBody
//import retrofit2.Response
//
//class FakeRemoteDataSource : RemoteDataSourceImpl {
//
//    var forecastResponse: ForecastResponse? = null
//    var weatherResponse: WeatherResponse? = null
//    var shouldReturnError: Boolean = false
//
//    override suspend fun getForecastData(
//        latitude: String,
//        longitude: String,
//        units: String,
//        lang: String
//    ): Response<ForecastResponse> {
//        return if (shouldReturnError) {
//            Response.error(
//                500,
//                ResponseBody.create(
//                    MediaType.parse("application/json"),
//                    "{\"error\":\"Failed to fetch forecast data\"}"
//                )
//            )
//        } else {
//            Response.success(forecastResponse)
//        }
//    }
//
//    override suspend fun getForecastDataByCityName(
//        cityName: String,
//        units: String,
//        lang: String
//    ): Response<ForecastResponse> {
//        return if (shouldReturnError) {
//            Response.error(
//                500,
//                ResponseBody.create(
//                    MediaType.parse("application/json"),
//                    "{\"error\":\"Failed to fetch forecast data by city name\"}"
//                )
//            )
//        } else {
//            Response.success(forecastResponse)
//        }
//    }
//
//    override suspend fun getWeatherData(
//        latitude: String,
//        longitude: String,
//        units: String,
//        lang: String
//    ): Response<WeatherResponse> {
//        return if (shouldReturnError) {
//            Response.error(
//                500,
//                ResponseBody.create(
//                    MediaType.parse("application/json"),
//                    "{\"error\":\"Failed to fetch weather data\"}"
//                )
//            )
//        } else {
//            Response.success(weatherResponse)
//        }
//    }
//
//    override suspend fun getWeatherDataByCityName(
//        cityName: String,
//        units: String,
//        lang: String
//    ): Response<WeatherResponse> {
//        return if (shouldReturnError) {
//            Response.error(
//                500,
//                ResponseBody.create(
//                    MediaType.parse("application/json"),
//                    "{\"error\":\"Failed to fetch weather data by city name\"}"
//                )
//            )
//        } else {
//            Response.success(weatherResponse)
//        }
//    }
//}
