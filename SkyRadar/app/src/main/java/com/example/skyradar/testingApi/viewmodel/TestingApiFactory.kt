package com.example.skyradar.testingApi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skyradar.model.Repository

class TestingApiFactory(
    private val _repo: Repository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(TestingApiViewModel::class.java)) {
            TestingApiViewModel(_repo) as T
        } else {
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}