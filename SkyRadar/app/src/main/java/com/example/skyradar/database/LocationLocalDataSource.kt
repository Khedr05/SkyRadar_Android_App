package com.example.skyradar.database

import com.example.skyradar.model.DatabasePojo
import kotlinx.coroutines.flow.Flow


interface LocationLocalDataSource {
    fun getFavoriteLocations(): Flow<List<DatabasePojo>>
    suspend fun addFavorite(location: DatabasePojo)
    suspend fun removeFavorite(location: DatabasePojo)
}