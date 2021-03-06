package com.example.nextrun.other

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import com.example.nextrun.services.Polyline
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit
import kotlin.math.round

object TrackingUtility {

    fun hasLocationsPermissions(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    fun calculatePolylineLength(polyline: Polyline): Float {
        var distance = 0f
        for (i in 0..polyline.size-2) {
            val pos1 = polyline[i]
            val pos2 = polyline[i+1]

            val result = FloatArray(1)

            Location.distanceBetween(
                pos1.latitude, pos1.longitude, pos2.latitude, pos2.longitude, result
            )
            distance += result[0]
        }

        return distance
    }

    fun getFormattedStopwatchTime(ms: Long, includeMillis: Boolean = false): String {
        var milliseconds = ms

        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        if (!includeMillis) {
            return "${if (hours < 10) "0" else ""}$hours:" +
                    "${if (minutes < 10) "0" else ""}$minutes:" +
                    "${if (seconds < 10) "0" else ""}$seconds"
        }

        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds /= 10

        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds:" +
                "${if (milliseconds < 10) "0" else ""}$milliseconds"

    }

    fun getFormattedDistance(distance: Float): String {
        var distanceMeters = distance
        var distanceString: String = ""
        if (distanceMeters < 1000) {
            // meters
            distanceString = String.format("%.0f", distanceMeters)
            return "${distanceString} m"
        } else {
            // kilometers
            distanceMeters = distanceMeters / 1000f
            distanceString = String.format("%.2f", distanceMeters)
            return "${distanceString} km"
        }
    }

    fun getFormattedPace(distance: Float, time: Long): String {
        var milliseconds = time
        val kilometers = distance / 1000f

        var millisecondsPerKm = round(milliseconds / kilometers).toLong()

        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisecondsPerKm)
        millisecondsPerKm -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisecondsPerKm)

        if (TimeUnit.MILLISECONDS.toSeconds(milliseconds) < 3 && distance < 10f) {
            return "-:--"
        }

        return "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds"
    }
}