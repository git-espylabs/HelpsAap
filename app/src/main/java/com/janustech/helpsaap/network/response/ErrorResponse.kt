package com.janustech.helpsaap.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val type: String,
    val title: String,
    val status: Int,
    val detail: String = ""
)