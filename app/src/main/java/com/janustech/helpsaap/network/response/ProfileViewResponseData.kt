package com.janustech.helpsaap.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileViewResponseData(
    val id: String = "",
    val cus_name: String = "",
    val phone_number: String = "",
    val password: String = "",
    val location_id: String = "",
    val businessname: String = "",
    val whatsapp: String = "",
    val website: String = "",
    val current_location: String = "",
    val photo: String = "",
    val email: String = "",
    val offerpercentage: String = "",
    val areaname: String = "",
    val lat: String = "",
    val long: String = "",
    val language: String = ""
)
