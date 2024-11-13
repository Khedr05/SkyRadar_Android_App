package com.example.skyradar.database

import com.example.skyradar.model.DatabasePojo
import kotlinx.coroutines.flow.Flow

class FakeLocalDataSource : LocationLocalDataSource {
    override fun getFavoriteLocations(): Flow<List<DatabasePojo>> {
        TODO("Not yet implemented")
    }

    override suspend fun addFavorite(location: DatabasePojo) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFavorite(location: DatabasePojo) {
        TODO("Not yet implemented")
    }

    override suspend fun updateFavorite(location: DatabasePojo) {
        TODO("Not yet implemented")
    }
}