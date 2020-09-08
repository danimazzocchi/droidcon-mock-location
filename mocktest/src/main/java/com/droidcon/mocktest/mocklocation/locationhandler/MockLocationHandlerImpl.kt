package com.droidcon.mocktest.mocklocation.locationhandler

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.droidcon.mocktest.app.domain.MockLocation
import com.droidcon.mocktest.support.build
import com.droidcon.mocktest.support.getNextCoordinatesToDestination
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MockLocationHandlerImpl(context: Context) :
    MockLocationHandler {

    val mainHandler = Handler(Looper.getMainLooper())
    val locationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    var mockLocationHandlerStatusLiveData: MutableLiveData<MockLocationHandlerStatus> =
        MutableLiveData(
            MockLocationHandlerStatus.STOPPED
        )
    private var mainPoints: List<MockLocation>? = null

    @SuppressLint("MissingPermission")
    override fun activate(mockLocationListener: MockLocationListener) {
        mainPoints?.let { mainPoints ->
            if (mockLocationHandlerStatusLiveData.value == MockLocationHandlerStatus.STOPPED && mainPoints.size > 1) {
                mockLocationHandlerStatusLiveData.postValue(MockLocationHandlerStatus.ACTIVATED)
                locationClient.setMockMode(true)
                    .addOnSuccessListener {
                        var i = 0
                        var currentPosition = mainPoints[i]
                        var currentDestination = mainPoints[i + 1]
                        mainHandler.post(object : Runnable {
                            override fun run() {
                                if (mockLocationHandlerStatusLiveData.value != MockLocationHandlerStatus.PAUSED && currentPosition != currentDestination) {
                                    currentPosition =
                                        currentPosition.getNextCoordinatesToDestination(
                                            currentDestination,
                                            currentDestination.speed,
                                            UPDATE_MOCK_LOCATION
                                        )
                                }
                                emitLocation(currentPosition, mockLocationListener)
                                if (currentPosition == currentDestination && i < mainPoints.size - 2) {
                                    //Go to next destination
                                    i++
                                    currentPosition = mainPoints[i]
                                    currentDestination = mainPoints[i + 1]
                                }
                                if (mockLocationHandlerStatusLiveData.value != MockLocationHandlerStatus.STOPPED) {
                                    mainHandler.postDelayed(this, UPDATE_MOCK_LOCATION)
                                }
                            }
                        })
                    }.addOnFailureListener { e ->
                        mockLocationListener.onLocationError(e)
                        stop()
                    }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun emitLocation(
        mockLocation: MockLocation,
        mockLocationListener: MockLocationListener
    ) {
        val location = Location(mockLocation.provider).build(mockLocation)
        locationClient
            .setMockLocation(location)
            .addOnSuccessListener {
                mockLocationListener.onLocationChanged(location)
            }.addOnFailureListener { e ->
                mockLocationListener.onLocationError(e)
                stop()
            }
    }

    @SuppressLint("MissingPermission")
    override fun stop() {
        mainHandler.removeCallbacksAndMessages(null)
        locationClient.setMockMode(false)
        mockLocationHandlerStatusLiveData.postValue(MockLocationHandlerStatus.STOPPED)
    }

    override fun playPause() {
        if (mockLocationHandlerStatusLiveData.value == MockLocationHandlerStatus.ACTIVATED || mockLocationHandlerStatusLiveData.value == MockLocationHandlerStatus.PAUSED) {
            mockLocationHandlerStatusLiveData.postValue(if (mockLocationHandlerStatusLiveData.value == MockLocationHandlerStatus.ACTIVATED) MockLocationHandlerStatus.PAUSED else MockLocationHandlerStatus.ACTIVATED)
        }
    }

    override fun statusLiveData(): LiveData<MockLocationHandlerStatus> {
        return mockLocationHandlerStatusLiveData
    }

    override fun setMapPoints(locations: List<MockLocation>) {
        if (mainPoints != locations) {
            mainPoints = locations
            stop()
        }
    }

    companion object {
        const val TAG = "MockHandler"
        const val UPDATE_MOCK_LOCATION = 1000L
    }
}