package com.janustech.helpsaap.network.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationListRequest(val place: String)
