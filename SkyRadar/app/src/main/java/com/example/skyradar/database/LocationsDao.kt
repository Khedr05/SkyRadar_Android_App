package com.example.skyradar.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.skyradar.model.DatabasePojo
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationsDao {
    @Query("SELECT * FROM favorites_table")
    fun getAllFavoritesLocation(): Flow<List<DatabasePojo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertnewLocation(location: DatabasePojo)

    @Delete
    suspend fun deleteCurrentLocation(location: DatabasePojo)

    @Update
    suspend fun updateLocation(location: DatabasePojo)
}