package com.droidcon.mocklocation.support

import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng

fun GoogleMap.drawCircle(latitude: Double, longitude:Double, radius: Double) {
    addCircle(CircleOptions().apply {
        center(LatLng(latitude, longitude))
        radius(radius)
        strokeColor(Color.RED)
        fillColor(0x30ff0000)
        strokeWidth(2f)
    })
}