package com.droidcon.mocklocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.droidcon.mocklocation.support.drawCircle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

private const val TAG = "MapsActivity"
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val viewModel: MapsViewModel by viewModels()

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).apply {
            getMapAsync(this@MapsActivity)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (checkForegroundLocationPermissions()) {
            mMap.isMyLocationEnabled = true
        } else {
            requestForegroundPermissions()
        }
        mMap.uiSettings.isZoomControlsEnabled = true
        viewModel.apply {
            uiGeofencesLiveData.observe(this@MapsActivity, Observer { uiGeofences ->
                uiGeofences.forEach { uiGeofence ->
                    mMap.drawCircle(uiGeofence.latitude, uiGeofence.longitude, uiGeofence.radius)
                }
            })
            uiCoordinatesLiveData.observe(this@MapsActivity, Observer { uiCoordinates ->
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            uiCoordinates.latitude,
                            uiCoordinates.longitude
                        ), 14f
                    )
                )
            })
            errorLiveEvent.observe(this@MapsActivity, Observer { event ->
                Toast.makeText(this@MapsActivity, event.peekContent(), Toast.LENGTH_SHORT).show()
            })
            onMapReady()
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkForegroundLocationPermissions(): Boolean {
        val fineLocationPermissionState = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        )
        return fineLocationPermissionState == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Requests the ACCESS_FINE_LOCATION permission to the user.
     */
    private fun requestForegroundPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        )
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when {
            grantResults.isEmpty() ->
                Log.d(TAG, "User interaction was cancelled.")
            grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                mMap.isMyLocationEnabled = true
            else -> {
                Log.d(TAG, "Permission denied")
            }
        }
    }
}
