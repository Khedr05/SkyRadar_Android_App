package com.example.skyradar.alarm.view

data class Alarm(
    val requestCode: Int,
    val timeInMillis: Long,
    var isActive: Boolean = true
)
