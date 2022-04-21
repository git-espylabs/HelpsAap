package com.janustech.helpsaap.network.response

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class MultipartApiResponse(
    val error: String = "",
    val data: String = "",
    val message: String = "",
){
    fun isResponseSuccess(): Boolean = (error.equals("false", true) && message.equals("Success", true))
}