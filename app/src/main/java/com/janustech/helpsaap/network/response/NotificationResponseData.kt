package com.janustech.helpsaap.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationResponseData(
    val id: String = "",
    val description: String = ""
)
