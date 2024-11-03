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

}