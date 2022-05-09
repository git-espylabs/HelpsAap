package com.janustech.helpsaap.model

data class UserData(
    val userId: String = "",
    val customerName: String = "",
    val phoneNumber: String = "",
    val whatsapp: String = "",
    val email: String = "",
    val website: String = "",
    val currentLocation: String = "",
    val photo: String = "",
    val otp: String = "",
    val password: String = ""
)
