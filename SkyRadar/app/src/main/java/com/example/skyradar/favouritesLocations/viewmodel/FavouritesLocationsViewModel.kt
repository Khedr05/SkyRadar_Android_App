package com.example.skyradar.favouritesLocations.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyradar.model.DatabasePojo
import com.example.skyradar.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavouritesLocationsViewModel(private val _repo: Repository) : ViewModel() {

    // StateFlow to hold favorite locations
    private val _favoriteLocations = MutableStateFlow<List<DatabasePojo>>(emptyList())
    val favoriteLocations: StateFlow<List<DatabasePojo>> get() = _favoriteLocations

    init {
        // Fetch favorite locations initially
        fetchFavoriteLocations()
    }

    // Method to fetch favorite locations
    private fun fetchFavoriteLocations() {
        viewModelScope.launch {
            _repo.getFavoriteLocations().collect { locations ->
                _favoriteLocations.value = locations
            }
        }
    }

    // Method to remove a location from favorites
    fun removeFavoriteLocation(location: DatabasePojo) {
        viewModelScope.launch {
            _repo.removeFavorite(location)
            fetchFavoriteLocations() // Refresh favorite locations
        }
    }
}
