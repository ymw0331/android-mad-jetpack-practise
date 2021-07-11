package com.wayneyong.distancetracker.ui.maps

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import com.wayneyong.distancetracker.R
import com.wayneyong.distancetracker.databinding.FragmentMapsBinding
import com.wayneyong.distancetracker.model.Result
import com.wayneyong.distancetracker.service.TrackerService
import com.wayneyong.distancetracker.util.Contants.ACTION_SERVICE_START
import com.wayneyong.distancetracker.util.Contants.ACTION_SERVICE_STOP
import com.wayneyong.distancetracker.util.ExtensionFunctions.disable
import com.wayneyong.distancetracker.util.ExtensionFunctions.enable
import com.wayneyong.distancetracker.util.ExtensionFunctions.hide
import com.wayneyong.distancetracker.util.ExtensionFunctions.show
import com.wayneyong.distancetracker.util.Permissions.hasBackgroundLocationPermission
import com.wayneyong.distancetracker.util.Permissions.requestBackgroundLocationPermission
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//if fields are not injected yet don't annotate first

class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMarkerClickListener, EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    val started = MutableLiveData(false)

    private lateinit var map: GoogleMap

    private var startTime = 0L
    private var stopTime = 0L

    private var locationList = mutableListOf<LatLng>()
    private var polylineList = mutableListOf<Polyline>()

    private var markerList = mutableListOf<Marker>()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_maps, container, false)
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.tracking = this

        binding.startButton.setOnClickListener {
            //check if we have background location access
            onStartButtonClicked()
        }

        binding.stopButton.setOnClickListener {
            onStopButtonClicked()
        }

        binding.resetButton.setOnClickListener {
            onResetButtonClicked()
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap!!
        map.isMyLocationEnabled = true
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMarkerClickListener(this)

        map.uiSettings.apply {
            isZoomControlsEnabled = false
            isZoomGesturesEnabled = false
            isRotateGesturesEnabled = false
            isTiltGesturesEnabled = false
            isCompassEnabled = false
            isScrollGesturesEnabled = false
        }
        observeTrackerService()
    }

    private fun observeTrackerService() { //observe mutable object from trackerService
        TrackerService.locationList.observe(viewLifecycleOwner, {
            if (it != null) {
                locationList = it
                if (locationList.size > 1) {
                    binding.stopButton.enable()
                }
                Log.e(
                    "LocationList",
                    locationList.toString()
                ) //draw a polyline based on locationList
                drawPolyline()
                followPolyline()
            }
        })
        //observe variables from TrackerService (started, startTime, stopTime)
        TrackerService.started.observe(viewLifecycleOwner, {
            started.value = it
        })

        TrackerService.startTime.observe(viewLifecycleOwner, {
            startTime = it
        })
        TrackerService.stopTime.observe(viewLifecycleOwner, {
            stopTime = it
            if (stopTime != 0L) {
                //will not be 0 when stopTime is retrieved
                showBiggerPicture()
                displayResults()
            }
        })
    }


    private fun drawPolyline() {
        val polyline = map.addPolyline(
            PolylineOptions().apply {
                width(10f)
                color(Color.BLUE)
                jointType(JointType.ROUND)
                startCap(ButtCap())
                endCap(ButtCap())
                addAll(locationList)
            }
        )
        polylineList.add(polyline) //add to polylist to store and delete after reset
    }

    private fun followPolyline() { //set camera position, everytime we receive new camera update, choose last item of locationList
        if (locationList.isNotEmpty()) {
            map.animateCamera(
                (CameraUpdateFactory.newCameraPosition(
                    MapUtil.setCameraPosition(locationList.last())
                )), 1000, null
            )
        }
    }


    private fun onStartButtonClicked() {
        if (hasBackgroundLocationPermission(requireContext())) {
            Log.d("MapsActivity", "Already Enabled onStartButtonClicked")
            startCountDown()
            //extension functions used
            binding.startButton.disable()
            binding.startButton.hide()
            binding.stopButton.show()

        } else {
            requestBackgroundLocationPermission(this)
        }
    }


    private fun onStopButtonClicked() {
        stopForegroundService()
        binding.stopButton.hide()
        binding.startButton.show()
//        binding.startButton.enable()

    }


    private fun onResetButtonClicked() {
        mapReset()
    }


    private fun startCountDown() {
        binding.timerTextView.show()
        binding.stopButton.disable()

        val timer: CountDownTimer = object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                //triggered every second
                val currentSecond = millisUntilFinished / 1000
                if (currentSecond.toString() == "0") {
                    binding.timerTextView.text = "GO"
                    binding.timerTextView.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.black)
                    )
                } else {
                    binding.timerTextView.text = currentSecond.toString()
                    binding.timerTextView.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.red)
                    )
                }
            }

            override fun onFinish() {
                //triggered when countDown timer finished
                //whenever we start service from map fragment, passing specific constant
                sendActionCommandToService(ACTION_SERVICE_START)
                binding.timerTextView.hide()
            }
        }
        timer.start()
    }

    private fun stopForegroundService() {
        binding.startButton.disable()
        sendActionCommandToService(ACTION_SERVICE_STOP)
    }

    private fun sendActionCommandToService(action: String) {
        Intent(
            requireContext(),
            TrackerService::class.java
        ).apply {
            this.action = action
            requireContext().startService(this)
        }
    }

    private fun showBiggerPicture() {
        //set bounds to show the whole track

        val bounds = LatLngBounds.Builder()
        for (location in locationList) {
            bounds.include(location)
        }
        map.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(), 100
            ), 2000, null
        )
        addMarker(locationList.first())
        addMarker(locationList.last())

    }

    private fun addMarker(position: LatLng) {
        val marker = map.addMarker(MarkerOptions().position(position))
        markerList.add(marker)
    }


    private fun displayResults() {
        val result = Result(
            MapUtil.calculateTheDistance(locationList), //pass in first and last location to cal
            MapUtil.calculateElapsedTime(startTime, stopTime)
        )
        lifecycleScope.launch {
            delay(2500)
            val directions = MapsFragmentDirections.actionMapsFragmentToResultFragment(result)
            findNavController().navigate(directions)
            binding.startButton.apply {
                hide() //using scoped functions
                enable()
            }
            binding.stopButton.hide()
            binding.resetButton.show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun mapReset() {
        //remove all polyline and animate to user location
        fusedLocationProviderClient.lastLocation.addOnCompleteListener {
            val lastKnownLocation = LatLng(
                it.result.latitude,
                it.result.longitude
            )

            map.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    MapUtil.setCameraPosition(lastKnownLocation)
                )
            )
            for (marker in markerList) {
                marker.remove()
            }

            locationList.clear()
            markerList.clear()
            binding.resetButton.hide()
            binding.startButton.show()
        }
    }


    override fun onMyLocationButtonClick(): Boolean {

        //Animate hint text view to fade out
        binding.hintTextView.animate().alpha(0f).duration = 1500
        lifecycleScope.launch {
            delay(2500)
            binding.hintTextView.hide()
            binding.startButton.show()
        }
        return false
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms[0])) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {

            requestBackgroundLocationPermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        onStartButtonClicked()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return true //to prevent user move camera the animation of the marker
    }
}