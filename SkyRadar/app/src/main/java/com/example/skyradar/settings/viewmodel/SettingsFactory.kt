package com.example.skyradar.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skyradar.model.Repository


class SettingsFactory(
    private val _repo: Repository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            SettingsViewModel(_repo) as T
        } else {
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}