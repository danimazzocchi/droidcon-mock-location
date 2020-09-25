package com.droidcon.mocklocation.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

/**
 * Created by Daniele Mazzocchi on 13/06/2020.
 * daniele.mazzocchi@accenture.com
 */

class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            Log.e(TAG, geofencingEvent.errorCode.toString())
            return
        }
        when (geofencingEvent.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Toast.makeText(context, "ENTER", Toast.LENGTH_SHORT).show()
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Toast.makeText(context, "EXIT", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val TAG = "GeofenceReceiver"
    }

}