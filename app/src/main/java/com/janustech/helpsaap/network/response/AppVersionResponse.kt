package com.janustech.helpsaap.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AppVersionResponse(
    val id: String = "",
    val version_code: String = "1",
    val version_name: String = "1",
    val app_type: String = "1"
)