package com.droidcon.mocktest.mocklocation.locationhandler

import android.location.Location

/**
 * Interface to receive last mocked location or error in mocking.
 */
interface MockLocationListener {

    /**
     * Receives the last mocked location.
     *
     * @param location is the mocked [Location].
     */
    fun onLocationChanged(location: Location)

    /**
     * Receives an error if the mocking fails.
     *
     * @param e the mocked [Exception].
     */
    fun onLocationError(e: Exception)
}