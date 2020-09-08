package com.droidcon.mocktest.app.domain

import kotlinx.coroutines.flow.Flow

/**
 * The Repository interface to handle mock locations.
 */
interface MockLocationRepository {
    suspend fun addLocation(mockLocation: MockLocation)
    suspend fun removeLocation(mockLocation: MockLocation)
    suspend fun updateLocation(mockLocation: MockLocation)
    suspend fun removeAllLocations()
    fun getLocations(): Flow<List<MockLocation>>
}