package com.droidcon.mocktest.app.data.source.local

import android.content.Context
import androidx.room.Room
import com.droidcon.mocktest.app.data.db.MockLocationDatabase
import com.droidcon.mocktest.app.data.source.MockLocationDataSource
import com.droidcon.mocktest.app.domain.MockLocation
import com.droidcon.mocktest.support.toDbEntity
import com.droidcon.mocktest.support.toMockLocations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class MockLocationLocalDataSource(context: Context) : MockLocationDataSource {

    private val mockLocationDao = Room.databaseBuilder(
        context,
        MockLocationDatabase::class.java,
        DATABASE_NAME
    ).build().mockLocationDao()

    override suspend fun addLocation(mockLocation: MockLocation) {
        mockLocation.creationDate = System.currentTimeMillis()
        mockLocationDao.insert(mockLocation.toDbEntity())
    }

    override suspend fun removeLocation(mockLocation: MockLocation) {
        mockLocationDao.delete(mockLocation.toDbEntity())
    }

    override suspend fun updateLocation(mockLocation: MockLocation) {
        mockLocation.creationDate = System.currentTimeMillis()
        mockLocationDao.insert(mockLocation.toDbEntity())
    }

    override fun getLocations(): Flow<List<MockLocation>> {
        return mockLocationDao.getAll().map { it.toMockLocations() }
    }

    override suspend fun removeAllLocations() {
        mockLocationDao.deleteAll()
    }

    companion object {
        const val DATABASE_NAME = "mock-locations-database"
    }
}