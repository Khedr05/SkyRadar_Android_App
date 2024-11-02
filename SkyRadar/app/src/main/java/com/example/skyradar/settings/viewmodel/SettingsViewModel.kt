package com.example.skyradar.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyradar.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val _repo: Repository) : ViewModel() {

    private val _language = MutableStateFlow<String?>(null)
    val language: StateFlow<String?> get() = _language

    private val _unit = MutableStateFlow<String?>(null)
    val unit: StateFlow<String?> get() = _unit

    init {
        viewModelScope.launch {
            _language.value = _repo.getLanguage()
            _unit.value = _repo.getUnit()
        }
    }

    fun saveLanguage(language: String) {
        _language.value = language
        viewModelScope.launch { _repo.saveLanguage(language) }
    }

    fun saveUnit(unit: String) {
        _unit.value = unit
        viewModelScope.launch { _repo.saveUnit(unit) }
    }
}
