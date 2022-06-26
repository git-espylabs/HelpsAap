package com.janustech.helpsaap.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class AboutUsResponse(
    val id: String = "",
    val about: String = "",
)
