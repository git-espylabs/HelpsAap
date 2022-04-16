package com.janustech.helpsaap.network.requests

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class LoginRequest(val email: String, val password: String){
    var emailId: String = email

    var uPassword: String = password
}