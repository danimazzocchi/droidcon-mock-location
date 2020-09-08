package com.droidcon.mocklocation

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.droidcon.mocktest.mocklocation.locationhandler.MockLocationHandler
import com.droidcon.mocktest.mocklocation.locationhandler.MockLocationHandlerImpl
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class MapsViewModel(application: Application): AndroidViewModel(application) {

    val uiGeofencesLiveData : MutableLiveData<List<UiGeofence>> = MutableLiveData()
    val uiCoordinatesLiveData: MutableLiveData<UiCoordinates> = MutableLiveData()
    val errorLiveEvent : LiveData<Event<String>>
        get() = _errorLiveEvent

    private val _errorLiveEvent = MutableLiveData<Event<String>>()
    private val geofenceManager: GeofenceManager = GeofenceManager(getApplication())
    private var mockLocationHandler: MockLocationHandler =
        MockLocationHandlerImpl(getApplication())

    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            uiCoordinatesLiveData.postValue(UiCoordinates(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude))
        }
    }

    @SuppressLint("MissingPermission")
    fun onMapReady() {
        geofenceManager.setupGeofences(GEOFENCES_COORDINATES)
            .addOnSuccessListener {
                val uiGeofences = arrayListOf<UiGeofence>()
                GEOFENCES_COORDINATES.forEach {
                    uiGeofences.add(UiGeofence(it.first, it.second, GEOFENCE_RADIUS.toDouble()))
                }
                uiGeofencesLiveData.postValue(uiGeofences)
            }
            .addOnErrorListener {
                _errorLiveEvent.value = Event("Error")
            }

        LocationServices.getFusedLocationProviderClient(getApplication() as Context).requestLocationUpdates(
            LocationRequest.create()?.apply {
                interval = 3000
                fastestInterval = 1500
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }, locationCallback
            , Looper.getMainLooper()
        )
    }
}