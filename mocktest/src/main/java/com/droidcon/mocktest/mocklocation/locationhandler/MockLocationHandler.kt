package com.droidcon.mocktest.mocklocation.locationhandler

import androidx.lifecycle.LiveData
import com.droidcon.mocktest.app.domain.MockLocation

    /**
     * The interface to handle the mock of the location.
     */
    interface MockLocationHandler {

        /**
         * LiveData of the [MockLocationHandlerStatus] of the Mock Location.
         */
        fun statusLiveData() : LiveData<MockLocationHandlerStatus>

        /**
         * Activate the mock of the location by starting from the first mock location added.
         */
        fun activate(mockLocationListener: MockLocationListener)

        /**
         * Stops the running location simulation.
         */
        fun stop()

        /**
         * If a simulation is running, paused the simulation to certain location.
         */
        fun playPause()

        /**
         * Set new waypoints for location simulation.
         */
        fun setMapPoints(locations: List<MockLocation>)
    }


