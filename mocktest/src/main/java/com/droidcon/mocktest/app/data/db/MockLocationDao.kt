package com.droidcon.mocktest.app.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * The Dao to handle locations on DB.
 */
@Dao
interface MockLocationDao {
    @Query("SELECT * FROM MOCK_LOCATIONS_TABLE ORDER BY creation_date ASC")
    fun getAll(): Flow<List<MockLocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mockLocationEntity: MockLocationEntity)

    @Delete
    suspend fun delete(mockLocationEntity: MockLocationEntity)

    @Query("DELETE FROM MOCK_LOCATIONS_TABLE")
    suspend fun deleteAll()
}