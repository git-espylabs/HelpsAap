package com.janustech.helpsaap.model

data class UserCategoriesDataModel(
    val id: String = "",
    val customer_id: String = "",
    val categoryid: String = "",
    val cat: String = "",
    var type: String = "0"
){
    override fun toString(): String {
        return cat
    }
}