package com.janustech.helpsaap.network.response

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ApiResponse<out T>(
    val error: String = "",
    val data: T,
    val message: String = "",
){
    fun isResponseSuccess(): Boolean = (error.equals("false", true) && message.equals("Success", true))

    fun isVerifyPhoneResponseSuccess(): Boolean = (error.equals("false", true) && message.equals("Already Exist", true))
}