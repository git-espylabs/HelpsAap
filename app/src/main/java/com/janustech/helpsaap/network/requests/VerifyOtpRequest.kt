package com.janustech.helpsaap.network.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifyOtpRequest(
    val customerid: String,
    val otp: String,

)
