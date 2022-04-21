package com.janustech.helpsaap.model

data class CategoryDataModel(
    val id: String = "",
    val category: String = "",
    val category_hindi: String = "",
    val category_mal: String = ""
){
    override fun toString(): String {
        return category
    }
}
