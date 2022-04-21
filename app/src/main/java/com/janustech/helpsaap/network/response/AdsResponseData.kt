package com.janustech.helpsaap.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AdsResponseData(
    val id: String = "",
    val cus_id: String = "",
    val ads_name: String = "",
    val ads_image: String = "",
    val status: String = "",
    val payment_refid: String = "",
    val publish_type: String = "",
    val public_loc: String = "",
)
