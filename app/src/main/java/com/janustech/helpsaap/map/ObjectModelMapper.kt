package com.janustech.helpsaap.map

import com.janustech.helpsaap.model.*
import com.janustech.helpsaap.network.requests.ProfileCategorySubmitRequest
import com.janustech.helpsaap.network.response.*

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
    password = password?:"",
    offerpercentage = offerpercentage?:"0",
    lat = lat?:"",
    long = long?:"",
    areaname = areaname?:"",
    language = language?:"",
    businessname = businessname

)

internal fun LanguageListResponseData.toLanguageDataModel() = LanguageDataModel(
    id = id,
    langImage = lang_image?:"",
    lang = lang?:""
)

internal fun LocationListResponseData.toLocationDataModel() = LocationDataModel(
    id = id,
    panchayath = panchayath,
    district = district?:"",
    state = state?:""
)

internal fun DealsOfDayResponseData.toDealsOfDayDataModel() = DealOfDayDataModel(
    id = id,
    cus_id = cus_id,
    poster_image = poster_image?:"",
    locations = locations?:"",
    status = status?:""
)

internal fun AdsResponseData.toAdsDataModel() = AdsDataModel(
    id = id,
    cus_id = cus_id,
    ads_name = ads_name?:"",
    ads_image = ads_image?:"",
    status = status?:"",
    payment_refid = payment_refid?:"",
    publish_type = publish_type?:"",
    public_loc = public_loc?:""
)

internal fun CategoryResponseData.toCategoryDataModel() = CategoryDataModel(
    id = id,
    category = category,
    category_hindi = category_hindi?:"",
    category_mal = category_mal?:""
)

internal fun CompanyResponseData.toCompanyDataModel() = CompanyDataModel(
    id = id,
    cus_name = cus_name,
    phone_number = phone_number?:"",
    password = password?:"",
    location_id = location_id?:"",
    businessname = businessname?:"",
    whatsapp = whatsapp?:"",
    website = website?:"",
    current_location = current_location?:"",
    photo = photo?:"",
    panc = panchayath?:"",
    dist = district?:"",
    address = if (panchayath != null && district !=null){
        "$panchayath, $district"
    }else if (panchayath != null){
        panchayath
    }else{
        "--"
    }
)

internal fun String.toProfileCategoryModel() = ProfileCategorySubmitRequest(
     categoryid = this
)

internal fun NotificationResponseData.toNotificationDataModel() = NotificationDataModel(
    id = id,
    description = description?:""
)

internal fun PostedAdsResponseData.toPostedAdDataModel() = PostedAdDataModel(
    id = id,
    cus_id = cus_id?:"",
    ads_name = ads_name?:"",
    start_date = start_date?:"",
    end_date = end_date?:"",
    ads_image = ads_image?:"",
    status = status?:"",
    payment_refid = payment_refid?:"",
    publish_type = publish_type?:"",
    public_loc = public_loc?:"",
    panchayath = panchayath?:"",
    district = district?:"",
    state = state?:""
)

