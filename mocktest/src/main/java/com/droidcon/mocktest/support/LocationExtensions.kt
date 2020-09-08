package com.droidcon.mocktest.support

import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.droidcon.mocktest.R
import com.droidcon.mocktest.app.domain.MockLocation
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import java.util.*

/**
 * Build a [Location] starting from a [MockLocation].
 *
 * @param mockLocation the [MockLocation] with parameters to build the [Location] to return.
 * @return a [Location] object with properties initialized.
 */
fun Location.build(mockLocation: MockLocation): Location {
    latitude = mockLocation.latitude
    longitude = mockLocation.longitude
    time = Date().time
    accuracy = mockLocation.accuracy.toFloat()
    speed = mockLocation.speed.toFloat()
    elapsedRealtimeNanos = System.nanoTime()
    return this
}

/**
 * Converts the [MockLocation] to a [LatLng] object.
 */
fun MockLocation.latLng(): LatLng {
    return LatLng(latitude, longitude)
}

/**
 * Open a dialog to insert speed and accuracy for the [MockLocation].
 *
 * @param context the Context.
 * @param onInputDone the callback that return the [MockLocation] with updated speed and accuracy.
 */
fun MockLocation.askForLocationData(
    context: Context,
    onInputDone: (MockLocation) -> Unit
) {
    AlertDialog.Builder(context).apply {
        setTitle(context.getString(R.string.locations_path_details))
        val viewInflated: View =
            LayoutInflater.from(context).inflate(R.layout.location_details_dialog, null)
        setView(viewInflated)
        val speedEditText = viewInflated.findViewById<EditText>(R.id.speed).apply {
            setText(
                "${SharedPreferenceUtil.getLastSpeed(
                    context
                )}"
            )
        }
        val accuracySpeedText = viewInflated.findViewById<EditText>(R.id.accuracy).apply {
            setText(
                "${SharedPreferenceUtil.getLastAccuracy(
                    context
                )}"
            )
        }
        setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
            speedEditText.text.toString().toIntOrNull()
                ?.let {
                    this@askForLocationData.speed = it
                    SharedPreferenceUtil.saveLastSpeed(
                        context,
                        it
                    )
                }
            accuracySpeedText.text.toString().toIntOrNull()
                ?.let {
                    this@askForLocationData.accuracy = it
                    SharedPreferenceUtil.saveLastAccuracy(
                        context,
                        it
                    )
                }
            onInputDone.invoke(this@askForLocationData)
        }
        setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }
    }.show()
}

/**
 * Return the next step [MockLocation] to reach a destination. The distance between a an element
 * and the next one is calculated starting from the speed and the update time.
 */
fun MockLocation.getNextCoordinatesToDestination(destination: MockLocation, speed: Int,
                                                 updateTime: Long) : MockLocation {
    val distanceBetween =
        SphericalUtil.computeDistanceBetween(this.latLng(), destination.latLng())
    val points = (distanceBetween / (speed.kmhToms() * (updateTime / 1000))).toInt()
    val fraction = 1.toDouble() / points
    return if (points <= 1) {
        destination
    } else {
        val intermediateLatLng =
            SphericalUtil.interpolate(this.latLng(), destination.latLng(), fraction)
        MockLocation(
            null,
            provider,
            intermediateLatLng.latitude,
            intermediateLatLng.longitude,
            destination.speed,
            destination.accuracy
        )
    }
}

/**
 * Converts the int from km/h to m/s.
 */
fun Int.kmhToms(): Int {
    return this * 5 / 18
}