package com.droidcon.mocktest.app.data.source

import com.droidcon.mocktest.app.domain.MockLocation
import kotlinx.coroutines.flow.Flow

/**
 * Data Source for mock locations.
 */
interface MockLocationDataSource {

    /**
     * Add location to the selected data source.
     *
     * @param mockLocation The [MockLocation] to add.
     */
    suspend fun addLocation(mockLocation: MockLocation)

    /**
     * Remove location from the data source.
     */
    suspend fun removeLocation(mockLocation: MockLocation)

    /**
     * Update location on the data source.
     */
    suspend fun updateLocation(mockLocation: MockLocation)

    /**
     * Remove all locations from the data source.
     */
    suspend fun removeAllLocations()

    /**
     * Get all locations from the data source   .
     */
    fun getLocations(): Flow<List<MockLocation>>
}