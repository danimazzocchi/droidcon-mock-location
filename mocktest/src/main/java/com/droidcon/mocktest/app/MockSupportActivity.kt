package com.droidcon.mocktest.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.droidcon.mocktest.R
import com.droidcon.mocktest.app.domain.MockLocation
import com.droidcon.mocktest.app.ui.MockSupportViewModel
import com.droidcon.mocktest.mocklocation.MockLocationService
import com.droidcon.mocktest.mocklocation.locationhandler.MockLocationHandler
import com.droidcon.mocktest.mocklocation.locationhandler.MockLocationHandlerStatus
import com.droidcon.mocktest.mocklocation.service.ForegroundService
import com.droidcon.mocktest.support.askForLocationData
import com.droidcon.mocktest.support.drawLine
import com.droidcon.mocktest.support.latLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_mock_support.*


private val MILAN = LatLng(45.464664, 9.188540)
private const val DEFAULT_CAMERA_ZOOM = 14f

/**
 * The only activity in this module.
 *
 * Note: This activity allows with a long press to configure markers on the map. It is possible to
 * remove all the markers from the map using the "CLEAR" button.
 *
 * Note: Each marker corresponds to a waypoint of the fake path consisting of mock locations.
 * Connected to the marker it is possible to set the speed and the accuracy of the position.
 * These settings will be applied to the path from the previous marker to the one on which these
 * two properties are set.
 *
 * Note: It is possible start, pause and stop the fake route by pressing the "ACTIVATE", "PAUSE"
 * and "STOP" buttons
 *
 * Note: The [ForegroundService] will be active throughout the activity life cycle. It will remain
 * active only if there is a route that is being simulated.
 */
class MockSupportActivity : AppCompatActivity(), OnMapReadyCallback {

    /**
     * The [MockSupportViewModel] to handle the storage of mocked locations.
     */
    private val viewModel: MockSupportViewModel by viewModels()

    /**
     * The [GoogleMap] provided by google play services.
     */
    private lateinit var mMap: GoogleMap

    /**
     * The [MockLocationService] with which the activity must communicate to configure the .
     */
    var mService: MockLocationService? = null

    /**
     * Init of [SupportMapFragment] and start of [ForegroundService]. The Service will be active at
     * least for the whole activity life cycle.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mock_support)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        ContextCompat.startForegroundService(this, Intent(this, ForegroundService::class.java))
    }

    /**
     * Unbind from the [ForegroundService] and stop it if the status of [MockLocationHandler]
     * is stopped.
     */
    override fun onDestroy() {
        if (mService?.mockLocationHandlerStatusLiveData()?.value == MockLocationHandlerStatus.STOPPED) {
            mService?.stopService()
        }
        unbindService()
        super.onDestroy()
    }

    /**
     * Bind the service, define long click listener to add markers, observe the
     * [MockSupportViewModel] to draw markers and add points to the service.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (checkForegroundLocationPermissions()) {
            mMap.isMyLocationEnabled = true
        }
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MILAN, DEFAULT_CAMERA_ZOOM))
        bindService()
        viewModel.apply {
            mMap.setOnMapLongClickListener { latLng ->
                MockLocation(
                    provider = LocationManager.NETWORK_PROVIDER,
                    latitude = latLng.latitude,
                    longitude = latLng.longitude
                ).askForLocationData(this@MockSupportActivity, onInputDone = {
                    askForNewMapPoint(it)
                })
            }
            clear.setOnClickListener { removeLocations() }
            markersLiveData.observe(this@MockSupportActivity, Observer { mockLocations ->
                mMap.clear()
                mockLocations.forEachIndexed { index, location ->
                    mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(location.latitude, location.longitude))
                            .title(index.toString())
                    )
                    if (mockLocations.size > index + 1) {
                        mMap.drawLine(location, mockLocations[index + 1])
                    }
                }
                if (mockLocations.isNotEmpty()) {
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            mockLocations[mockLocations.size - 1].latLng(),
                            DEFAULT_CAMERA_ZOOM
                        )
                    )
                }
            })
        }
    }

    /**
     * Monitors the state of the connection to the service. When the service is connected initialize
     * the UI depending on the status of the [MockLocationService]
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
            val binder = iBinder as ForegroundService.ServiceBinder
            mService = binder.service
            playPause.setOnClickListener {
                mService?.playPauseMockLocation()
            }
            mService?.mockLocationHandlerStatusLiveData()?.value?.let {
                redirectStatus(it)
            }
            mService?.apply {
                mockLocationHandlerStatusLiveData().observe(
                    this@MockSupportActivity,
                    Observer { status ->
                        redirectStatus(status)
                    })
                mockLocationLiveData().observe(this@MockSupportActivity, Observer { location ->
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                location.latitude,
                                location.longitude
                            ), 15f
                        )
                    )
                })
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mService = null
        }
    }

    /**
     * Redirect the UI depending on the [MockLocationHandlerStatus].
     */
    @SuppressLint("MissingPermission")
    private fun redirectStatus(mockLocationHandlerStatus: MockLocationHandlerStatus) {
        when (mockLocationHandlerStatus) {
            MockLocationHandlerStatus.ACTIVATED -> {
                moveToMarkers.visibility = View.GONE
                playPause.visibility = View.VISIBLE
                playPause.text = getString(R.string.pause)
                activate.text = getString(R.string.stop)
                activate.setOnClickListener {
                    mService?.stopMockLocation()
                }
                clear.visibility = View.GONE
            }
            MockLocationHandlerStatus.PAUSED -> {
                moveToMarkers.visibility = View.GONE
                playPause.visibility = View.VISIBLE
                playPause.text = getString(R.string.resume)
                activate.text = getString(R.string.stop)
                clear.visibility = View.GONE
            }
            MockLocationHandlerStatus.STOPPED -> {
                moveToMarkers.visibility = View.VISIBLE
                moveToMarkers.setOnClickListener { moveToFirstMarker() }
                playPause.visibility = View.GONE
                activate.text = getString(R.string.activate)
                activate.setOnClickListener {

                    if (checkForegroundLocationPermissions()) {
                        mMap.isMyLocationEnabled = true
                    }
                    mService?.activateMockLocation()
                }
                clear.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Used to bind to our service class
     */
    private fun bindService() {
        Intent(this, ForegroundService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    /**
     * Used to unbind and stop our service class
     */
    private fun unbindService() {
        Intent(this, ForegroundService::class.java).also {
            unbindService(serviceConnection)
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
     * Move the camera to the first marker.
     */
    private fun moveToFirstMarker() {
        viewModel.markersLiveData.value?.let { markers ->
            if (markers.isNotEmpty()) {
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        markers[0].latLng(),
                        DEFAULT_CAMERA_ZOOM
                    )
                )
            }
        }
    }
}
