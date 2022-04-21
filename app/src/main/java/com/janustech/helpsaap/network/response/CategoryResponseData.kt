package com.janustech.helpsaap.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryResponseData(
    val id: String = "",
    val category: String = "",
    val category_hindi: String = "",
    val category_mal: String = ""
)
