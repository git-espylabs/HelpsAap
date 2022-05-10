package com.janustech.helpsaap.model

data class LanguageDataModel(
    val id: String = "",
    val langImage: String = "",
    val lang: String = ""
){
    override fun toString(): String {
        return lang
    }
}
