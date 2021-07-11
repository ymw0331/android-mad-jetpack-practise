package com.wayneyong.distancetracker.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.wayneyong.distancetracker.ui.maps.MapUtil
import com.wayneyong.distancetracker.util.Contants.ACTION_SERVICE_START
import com.wayneyong.distancetracker.util.Contants.ACTION_SERVICE_STOP
import com.wayneyong.distancetracker.util.Contants.LOCATION_FASTEST_UPDATE_INTERVAL
import com.wayneyong.distancetracker.util.Contants.LOCATION_UPDATE_INTERVAL
import com.wayneyong.distancetracker.util.Contants.NOTIFICATION_CHANNEL_ID
import com.wayneyong.distancetracker.util.Contants.NOTIFICATION_CHANNEL_NAME
import com.wayneyong.distancetracker.util.Contants.NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrackerService : LifecycleService() {

    lateinit var newLatLng: LatLng

    //handle constant that defined in constant
    @Inject
    lateinit var notification: NotificationCompat.Builder

    @Inject
    lateinit var notificationManager: NotificationManager

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val started = MutableLiveData<Boolean>()

        val startTime = MutableLiveData<Long>()
        val stopTime = MutableLiveData<Long>()

        val locationList = MutableLiveData<MutableList<LatLng>>()

    }


    private fun setInitialValues() {
        started.postValue(false)
        startTime.postValue(0L)
        stopTime.postValue(0L)

        locationList.postValue(mutableListOf())
    }


    //pass as one of the parameter for setLocationUpdate() below
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            result?.locations?.let { locations ->
                for (location in locations) { //location will be stored in mutable data
                    updateLocationList(location)
                    Log.e("TrackerService", newLatLng.toString())
                    updateNotificationPeriodically()
                }
            }
        }
    }


    private fun updateLocationList(location: Location) { //add new latlng to locationList
        newLatLng = LatLng(location.latitude, location.longitude)
        locationList.value?.apply {
            add(newLatLng)
            locationList.postValue(this)
        }
    }


    override fun onCreate() {
        setInitialValues()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //triggered every time service start from mapFragment

        intent?.let {
            when (it.action) {
                //this action is started when buttonStartClicked function clicked in MapsFragment
                ACTION_SERVICE_START -> {
                    started.postValue(true)
                    startForegroundService()
                    startLocationUpdates()
                }
                ACTION_SERVICE_STOP -> {
                    started.postValue(false)
                    //implement logic to stop foreground service
                    stopForegroundService()
                }
                else -> {

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notification.build())

    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {

        val locationRequest = LocationRequest().apply {
            interval = LOCATION_UPDATE_INTERVAL //setting interval by using constant , 4sec
            fastestInterval = LOCATION_FASTEST_UPDATE_INTERVAL //2sec
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()

        )
        startTime.postValue(System.currentTimeMillis())
        //ellapseTime is the diff between start and stop foreground service
    }

    private fun stopForegroundService() {
        removeLocationUpdates()
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            NOTIFICATION_ID
        )
        stopForeground(true)
        stopSelf()
        stopTime.postValue(System.currentTimeMillis())
    }

    private fun removeLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun updateNotificationPeriodically() {

        notification.apply {
            setContentTitle("Distance Travelled")
            setContentText(locationList.value?.let { MapUtil.calculateTheDistance(it) } + "km")
        }
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }


    //mandatory for api 26 or higher
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel) //injected on top
        }
    }
}