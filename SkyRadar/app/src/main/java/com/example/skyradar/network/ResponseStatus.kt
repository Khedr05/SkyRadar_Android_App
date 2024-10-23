package com.example.skyradar.network

import com.example.skyradar.model.ForecastResponse

//sealed class ResponseStatus {
//    class Success(val requestedData: ForecastResponse): ResponseStatus()
//    class Failure(val errorMessage: String) : ResponseStatus()
//    object Loading : ResponseStatus()
//}


//sealed class ResponseStatus<out T> {
//    class Success<out T>(val requestedData: T) : ResponseStatus<T>()
//    class Failure(val errorMessage: String) : ResponseStatus<Nothing>()
//    object Loading : ResponseStatus<Nothing>()
//}


sealed class ResponseStatus<out T> {
    data class Success<out T>(val requestedData: T) : ResponseStatus<T>()
    data class Failure(val errorMessage: String) : ResponseStatus<Nothing>()
    object Loading : ResponseStatus<Nothing>()
}
