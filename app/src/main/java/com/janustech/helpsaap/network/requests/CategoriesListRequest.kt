package com.janustech.helpsaap.network.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoriesListRequest(val category: String, var lang: String = "2")