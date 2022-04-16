package com.janustech.helpsaap.map

import com.janustech.helpsaap.model.UserData
import com.janustech.helpsaap.network.response.LoginResponseData

internal fun LoginResponseData.toUserData() = UserData(
    userId = id?:"0",
    customerName = cus_name?:"0",
    phoneNumber = phone_number?:"0",
    whatsapp = whatsapp?:"0",
    email  = email?:"0",
    website = website?:"0",
    currentLocation  = current_location?:"",
    photo  = photo?:"",
    otp  = otp?:"",
)