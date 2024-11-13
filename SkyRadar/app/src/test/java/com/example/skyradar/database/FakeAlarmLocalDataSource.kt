
package com.example.skyradar.database

import com.example.skyradar.model.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class FakeAlarmLocalDataSource : AlarmLocalDataSource {

    private val alarms = mutableListOf<Alarm>()

    override fun getAlarms(): Flow<List<Alarm>> = flowOf(alarms)

    override suspend fun insertAlarm(alarm: Alarm) {
        alarms.add(alarm)
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        alarms.remove(alarm)
    }
}
