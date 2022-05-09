package com.janustech.helpsaap.network.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EditProfileRequest(
    val customer_id: String,
    val cusname: String,
    val email: String,
    val categorylist: List<ProfileCategorySubmitRequest>
)