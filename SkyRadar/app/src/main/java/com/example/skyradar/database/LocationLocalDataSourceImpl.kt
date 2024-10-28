package com.example.skyradar.database

import android.content.Context
import com.example.skyradar.model.DatabasePojo
import kotlinx.coroutines.flow.Flow

class LocationLocalDataSourceImpl(private val context: Context) : LocationLocalDataSource {

    private val locationsDao: LocationsDao = AppDatabase.getDatabase(context).productDao()


    override fun getFavoriteLocations(): Flow<List<DatabasePojo>> {
        return locationsDao.getAllFavoritesLocation()
    }

    override suspend fun addFavorite(location: DatabasePojo) {
        return locationsDao.insertnewLocation(location)
    }

    override suspend fun removeFavorite(location: DatabasePojo) {
        return locationsDao.deleteCurrentLocation(location)
    }


    companion object {
        @Volatile
        private var INSTANCE: LocationLocalDataSource? = null

        fun getInstance(context: Context): LocationLocalDataSource {
            return INSTANCE ?: synchronized(this) {
                val instance = LocationLocalDataSourceImpl(context)
                INSTANCE = instance
                instance
            }
        }
    }

}