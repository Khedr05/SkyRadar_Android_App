package com.example.skyradar.map.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.osmdroid.util.GeoPoint

class MapViewModel : ViewModel() {

    private val _selectedLocation = MutableStateFlow<GeoPoint?>(null)
    val selectedLocation: StateFlow<GeoPoint?> get() = _selectedLocation

    fun updateSelectedLocation(location: GeoPoint) {
        _selectedLocation.value = location
    }
}