package com.janustech.helpsaap.network.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddOfferRequest(
    val customer_id: String,
    val percentage: String
) {
}