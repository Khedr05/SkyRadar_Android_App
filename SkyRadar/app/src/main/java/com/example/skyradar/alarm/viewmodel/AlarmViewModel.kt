package com.example.skyradar.alarm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyradar.model.Alarm
import com.example.skyradar.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AlarmViewModel(private val _repo: Repository) : ViewModel() {

    // StateFlow to hold favorite locations
    private val _alarm = MutableStateFlow<List<Alarm>>(emptyList())
    val alarm: StateFlow<List<Alarm>> get() = _alarm

    init {
        // Fetch favorite locations initially
        fetchAlarm()
    }

    // Method to fetch favorite locations
    private fun fetchAlarm() {
        viewModelScope.launch {
            _repo.getAlarms().collect { alarm ->
                _alarm.value = alarm
            }
        }
    }

    // Method to remove a location from favorites
    fun insertAlarm(alarm: Alarm){
        viewModelScope.launch {
            _repo.insertAlarm(alarm)
            fetchAlarm() // Refresh favorite locations
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            _repo.deleteAlarm(alarm)
            fetchAlarm() // Refresh favorite locations
        }
    }
}
