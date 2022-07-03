package com.janustech.helpsaap.map

import com.janustech.helpsaap.model.*
import com.janustech.helpsaap.network.requests.ProfileCategorySubmitRequest
import com.janustech.helpsaap.network.requests.UserCategoriesResponse
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
    businessname = businessname?:""

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
    category = category
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
    address = getAddressWithData(panchayath, district, state),
    lat = lat?:"",
    longi = long?:"",
    offerpercentage = offerpercentage?:"",
    areaname = areaname?:"",
    language = language?:""
)

fun getAddressWithData(panch: String, dist: String, state: String): String{
    var addr = ""
    if (panch != null && panch.isNotEmpty()){
        addr += panch
    }
    if (dist != null && dist.isNotEmpty()){
        addr = "$addr, $dist"
    }
    if (state != null && state.isNotEmpty()){
        addr  = "$addr, $state"
    }

    return addr;
}

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

internal fun ProfileViewResponseData.toProfileViewDataModel() = ProfileViewDataModel(
    id = id,
    cus_name = cus_name?:"",
    phone_number = phone_number?:"",
    password = password?:"",
    location_id = location_id?:"",
    businessname = businessname?:"",
    whatsapp = whatsapp?:"",
    website = website?:"",
    current_location = current_location?:"",
    photo = photo?:"",
    email = email?:"",
    offerpercentage = offerpercentage?:"",
    areaname = areaname?:"",
    lat = lat?:"",
    long = long?:"",
    language = language?:""
)

internal fun UserCategoriesResponse.toCategoryDataModel() = CategoryDataModel(
    id = categoryid,
    category = cat?:"",
    type = "0"
)

internal fun UserCategoriesResponse.toUserCategoryDataModel() = UserCategoriesDataModel(
    id = id,
    customer_id = customer_id?:"",
    categoryid = categoryid?:"",
    cat = cat?:"",
    type = "0"
)

internal fun CategoryDataModel.toUserCategoryDataModel() = UserCategoriesDataModel(
    id = id,
    customer_id = "",
    categoryid = id?:"",
    cat = category?:"",
    type = "1"
)

