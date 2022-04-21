package com.janustech.helpsaap.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DealsOfDayResponseData(
    val id: String = "",
    val cus_id: String = "",
    val poster_image: String = "",
    val locations: String = "",
    val status: String = ""
)