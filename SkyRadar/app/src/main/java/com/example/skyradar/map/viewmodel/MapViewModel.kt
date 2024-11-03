package com.example.skyradar.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyradar.model.DatabasePojo
import com.example.skyradar.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class MapViewModel(private val _repo: Repository) : ViewModel() {

    private val _selectedLocation = MutableStateFlow<GeoPoint?>(null)
    val selectedLocation: StateFlow<GeoPoint?> get() = _selectedLocation

//    fun addFavoriteLocation(location: DatabasePojo) {
//        viewModelScope.launch {
//            _repo.addFavorite(location)
//        }
//    }
}