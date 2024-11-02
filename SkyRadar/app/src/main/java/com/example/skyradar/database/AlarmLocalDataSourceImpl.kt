package com.example.skyradar.database

import android.content.Context
import com.example.skyradar.model.Alarm
import kotlinx.coroutines.flow.Flow

class AlarmLocalDataSourceImpl(private val context: Context) : AlarmLocalDataSource {

    private val alarmDao: AlarmDao = AlarmDatabase.getDatabase(context).alarmDao()

    override suspend fun insertAlarm(alarm: Alarm) {
        alarmDao.insert(alarm)
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm)
    }

    override fun getAlarms(): Flow<List<Alarm>> {
        return alarmDao.getAlarms()
    }


    companion object {
        @Volatile
        private var INSTANCE: AlarmLocalDataSource? = null

        fun getInstance(context: Context): AlarmLocalDataSource {
            return INSTANCE ?: synchronized(this) {
                val instance = AlarmLocalDataSourceImpl(context)
                INSTANCE = instance
                instance
            }
        }
    }

}
