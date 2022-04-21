package com.janustech.helpsaap.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationListResponseData(
    val id: String = "",
    val panchayath: String = "",
    val district: String = "",
    val state: String = ""
)
