package com.wayneyong.distancetracker.ui.maps

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.wayneyong.distancetracker.service.TrackerService.Companion.locationList
import java.text.DecimalFormat

object MapUtil {

    fun setCameraPosition(location: LatLng): CameraPosition {
        return CameraPosition.Builder()
            .target(location)
            .zoom(18f)
            .build()
    }

    //used this function in ResultFragment (trackerService to receive startTime and stopTime)
    fun calculateElapsedTime(startTime: Long, stopTime: Long): String {
        val elapsedTime = stopTime - startTime

        val seconds = (elapsedTime / 1000).toInt() % 60
        val minutes = (elapsedTime / 1000 * 60) % 60
        val hours = (elapsedTime / (1000 * 60 * 60) % 24)

        return "$hours:$minutes:$seconds"
    }

    fun calculateTheDistance(locationList: MutableList<LatLng>): String {
        if (locationList.size > 1) {
            val meters =
                SphericalUtil.computeDistanceBetween(
                    locationList.first(),
                    locationList.last()
                ) //distance between first location and last

            val kilometers = meters / 1000
            return DecimalFormat("#.##").format(kilometers)

        }
        return "0.00"  //size < 1
    }


}