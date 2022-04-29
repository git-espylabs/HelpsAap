package com.janustech.helpsaap.utils

import com.janustech.helpsaap.model.LocationDataModel

interface EditLocationListener {
    fun onLocationSelected(location: LocationDataModel)
}