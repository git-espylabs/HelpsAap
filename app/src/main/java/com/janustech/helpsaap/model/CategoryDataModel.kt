package com.janustech.helpsaap.model

import com.janustech.helpsaap.preference.AppPreferences

data class CategoryDataModel(
    val id: String = "",
    val category: String = ""
){
    override fun toString(): String {
        return category
    }
}
