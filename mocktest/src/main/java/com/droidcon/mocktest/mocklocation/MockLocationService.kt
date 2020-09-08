package com.droidcon.mocktest.mocklocation

import android.location.Location
import androidx.lifecycle.LiveData
import com.droidcon.mocktest.mocklocation.locationhandler.MockLocationHandlerStatus

/**
 * Interface to handle the mock of the location.
 */
interface MockLocationService {

    /**
     * LiveData of the [MockLocationHandlerStatus] of the Mock Location.
     */
    fun mockLocationHandlerStatusLiveData(): LiveData<MockLocationHandlerStatus>

    /**
     * LiveData of the last mocked location [Location].
     */
    fun mockLocationLiveData(): LiveData<Location>

    /**
     * Activate the mock of the location by starting from the first mock location added.
     */
    fun activateMockLocation()

    /**
     * If a simulation is running, paused the simulation to certain location.
     */
    fun playPauseMockLocation()

    /**
     * Stops the running location simulation.
     */
    fun stopMockLocation()

    /**
     * Stops the core service.
     */
    fun stopService()
}