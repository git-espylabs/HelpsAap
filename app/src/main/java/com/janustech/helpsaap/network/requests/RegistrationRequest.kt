package com.janustech.helpsaap.network.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationRequest(val email: String, val password: String)