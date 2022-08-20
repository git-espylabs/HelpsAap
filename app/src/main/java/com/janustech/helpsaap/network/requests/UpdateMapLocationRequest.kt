package com.janustech.helpsaap.network.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateMapLocationRequest(
    val customerid: String,
    val lat: String,
    val long: String,
)
