package com.janustech.helpsaap.location

import android.location.Location

interface GpsListener {
    fun onLocationUpdate(location: Location?)
    fun onLocationDisabled()
}