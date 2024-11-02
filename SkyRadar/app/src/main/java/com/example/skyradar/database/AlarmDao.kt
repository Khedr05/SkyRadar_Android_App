package com.example.skyradar.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.skyradar.model.Alarm
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Insert
    suspend fun insert(alarm: Alarm)

    @Query("SELECT * FROM alarms")
    fun getAlarms(): Flow<List<Alarm>>
}
