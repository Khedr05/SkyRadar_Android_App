package com.example.skyradar

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import java.util.Date
import java.util.Locale

object Helpers {

    fun formatTimestamp(timestamp: Long?): String {
        if (timestamp == null) return "N/A"

        // Convert from Unix timestamp to Date
        val date = Date(timestamp * 1000) // Convert seconds to milliseconds

        // Create a SimpleDateFormat for formatting the date
        val format = SimpleDateFormat("hh:mm a") // For 12-hour format
        format.timeZone = TimeZone.getDefault() // Set to the default time zone

        return format.format(date)
    }

    fun getHourFromUnixTime(unixTime: Long): Int {
        val date = Date(unixTime * 1000L)
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date
        return calendar.get(java.util.Calendar.HOUR_OF_DAY)  // Get hour in 24-hour format
    }

     fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getMeasurementString(
        isWind: Boolean,
        lang: String = "en",
        unit: String = "metric"
    ): String {
        return if (isWind) {
            // Wind measurement
            when (unit) {
                "imperial" -> if (lang == "ar") "ميل/س" else "mph"  // Miles per hour for Imperial
                "metric" -> if (lang == "ar") "م/ث" else "m/s"      // Meters per second for Metric
                else -> if (lang == "ar") "م/ث" else "m/s"          // Default to m/s for Standard
            }
        } else {
            // Temperature measurement
            when (unit) {
                "imperial" -> if (lang == "ar") "ف" else "F"       // Fahrenheit
                "metric" -> if (lang == "ar") "س" else "C"         // Celsius
                else -> if (lang == "ar") "ك" else "K"             // Kelvin (Standard)
            }
        }
    }


}