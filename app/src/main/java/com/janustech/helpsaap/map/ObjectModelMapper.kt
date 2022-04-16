package com.janustech.helpsaap.map

import com.janustech.helpsaap.model.LanguageDataModel
import com.janustech.helpsaap.model.UserData
import com.janustech.helpsaap.network.response.LanguageListResponseData
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

internal fun LanguageListResponseData.toLanguageDataModel() = LanguageDataModel(
    id = id,
    langImage = lang_image,
    lang = lang
)

