package com.droidcon.mocklocation.geofence

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.droidcon.mocklocation.support.GEOFENCE_RADIUS
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofenceManager(private val context: Context) {

    private val geofencingClient = LocationServices.getGeofencingClient(context)
    private var onSuccess: (() -> Unit)? = null
    private var onError: ((e: Exception) -> Unit)? = null

    fun addOnSuccessListener(onSuccessListener: () -> Unit): GeofenceManager {
        onSuccess =  onSuccessListener
        return this
    }

    fun addOnErrorListener(onErrorListener: (e: Exception) -> Unit): GeofenceManager {
        onError =  onErrorListener
        return this
    }

    @SuppressLint("MissingPermission")
    fun setupGeofences(geofencesCoordinates: Array<Pair<Double, Double>>) : GeofenceManager {
        geofencingClient.let { geofencingClient ->
            geofencingClient.addGeofences(getGeofencingRequest(geofencesCoordinates), geofencePendingIntent)
                ?.addOnSuccessListener {
                    onSuccess?.invoke()
                }?.addOnFailureListener {
                    onError?.invoke(it)
                }
        }
        return this
    }

    private fun getGeofencingRequest(geofencesCoordinates: Array<Pair<Double, Double>>): GeofencingRequest {
        val geofencesList = arrayListOf<Geofence>()
        geofencesCoordinates.forEach {
            geofencesList.add(buildGeofence(it.first, it.second))
        }
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofencesList)
        }.build()
    }

    private fun buildGeofence(lat: Double, lng: Double): Geofence {
        return Geofence.Builder()
            .setRequestId("$lat$lng")
            .setCircularRegion(lat, lng,
                GEOFENCE_RADIUS
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceReceiver::class.java)
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}

