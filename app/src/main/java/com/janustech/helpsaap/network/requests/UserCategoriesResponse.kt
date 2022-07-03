package com.janustech.helpsaap.network.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserCategoriesResponse(
    val id: String = "",
    val customer_id: String = "",
    val categoryid: String = "",
    val cat: String = ""
)