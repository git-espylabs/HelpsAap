package com.janustech.helpsaap.network.response

data class PostedAdsResponseData(
    val id: String = "",
    val cus_id: String = "",
    val ads_name: String = "",
    val start_date: String = "",
    val end_date: String = "",
    val ads_image: String = "",
    val status: String = "",
    val payment_refid: String = "",
    val publish_type: String = "",
    val public_loc: String = "",
    val panchayath: String = "",
    val district: String = "",
    val state: String = ""
)
