package com.droidcon.mocktest.app.data

import com.droidcon.mocktest.app.data.source.MockLocationDataSource
import com.droidcon.mocktest.app.domain.MockLocation
import com.droidcon.mocktest.app.domain.MockLocationRepository
import kotlinx.coroutines.flow.Flow

class MockLocationRepositoryImpl(private val mockLocationDataSource: MockLocationDataSource) :
    MockLocationRepository {
    override suspend fun addLocation(mockLocation: MockLocation) =
        mockLocationDataSource.addLocation(mockLocation)

    override suspend fun removeLocation(mockLocation: MockLocation) =
        mockLocationDataSource.removeLocation(mockLocation)

    override suspend fun updateLocation(mockLocation: MockLocation) =
        mockLocationDataSource.updateLocation(mockLocation)

    override suspend fun removeAllLocations() = mockLocationDataSource.removeAllLocations()

    override fun getLocations(): Flow<List<MockLocation>> = mockLocationDataSource.getLocations()
}