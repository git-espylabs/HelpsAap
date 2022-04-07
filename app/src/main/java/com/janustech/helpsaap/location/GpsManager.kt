package com.janustech.helpsaap.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.*
import java.lang.Exception


class GpsManager(var gpsListener: GpsListener?, var activity: Activity) {
    var currentLocation: Location? = null
    var lasKnownLocation: Location? = null

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.locations?.last()?.run {
                currentLocation = this
                stopListening()
                if (null == lasKnownLocation && currentLocation != null){
                    lasKnownLocation = this
                    gpsListener?.onLocationUpdate(lasKnownLocation)
                }
            }

        }
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 3000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        if (isLocationEnabled().not()){
            gpsListener?.onLocationDisabled()
            return
        }
        with(LocationServices.getFusedLocationProviderClient(activity.applicationContext)){
            lastLocation.addOnSuccessListener(activity) { location ->
                if (location != null) {
                    currentLocation = location
                    lasKnownLocation = location
                    gpsListener?.onLocationUpdate(location)
                }else{
                    startListening()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startListening() {
        with(LocationServices.getFusedLocationProviderClient(activity.applicationContext)) {
            requestLocationUpdates(
                createLocationRequest(),
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun isLocationEnabled(): Boolean{
        val lm = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        return gps_enabled && network_enabled
    }

    fun stopListening() {
        LocationServices.getFusedLocationProviderClient(activity.applicationContext).removeLocationUpdates(locationCallback)
    }

    fun setGpsCallback(gpsListener: GpsListener?) {
        this.gpsListener = gpsListener
    }
}