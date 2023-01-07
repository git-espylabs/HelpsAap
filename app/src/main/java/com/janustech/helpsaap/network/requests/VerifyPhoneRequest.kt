package com.janustech.helpsaap.network.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class VerifyPhoneRequest(
    val phonenumber: String
)