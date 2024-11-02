package com.example.skyradar.database

import com.example.skyradar.model.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmLocalDataSource {
    suspend fun insertAlarm(alarm: Alarm)
    suspend fun deleteAlarm(alarm: Alarm)
    fun getAlarms(): Flow<List<Alarm>>
}
