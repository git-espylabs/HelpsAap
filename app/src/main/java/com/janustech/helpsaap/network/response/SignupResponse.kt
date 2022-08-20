package com.janustech.helpsaap.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignupResponse(
    val id: String = "",
    val phone_number: String = "",
    val password: String = "",
    val cus_name: String = "",
    val email: String = "",
    val location_id: String = "",
    val businessname: String = "",
    val whatsapp: String = "",
    val webesite: String = "",
    val lat: String = "",
    val long: String = "",
    val areaname: String = "",
    val language: String = "",
    val offerpercentage: String = "",
    val photo: String = ""
)
