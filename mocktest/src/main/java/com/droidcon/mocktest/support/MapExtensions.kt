package com.droidcon.mocktest.support

import com.droidcon.mocktest.app.domain.MockLocation
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

fun GoogleMap.drawLine(origin: MockLocation, destination: MockLocation) {
    addPolyline(
        PolylineOptions().add(
            LatLng(origin.latitude, origin.longitude),
            LatLng(destination.latitude, destination.longitude)
        )
    )
}