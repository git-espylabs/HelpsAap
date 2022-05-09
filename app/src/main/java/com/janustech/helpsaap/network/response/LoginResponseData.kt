package com.janustech.helpsaap.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponseData(
    val id: String = "",
    val cus_name: String = "",
    val phone_number: String = "",
    val whatsapp: String = "",
    val email: String = "",
    val website: String = "",
    val current_location: String = "",
    val photo: String = "",
    val otp: String = "",
    val password: String = ""
)