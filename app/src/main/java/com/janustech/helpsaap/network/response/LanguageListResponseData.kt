package com.janustech.helpsaap.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LanguageListResponseData(
    val id: String = "",
    val lang_image: String = "",
    val lang: String = ""
)
