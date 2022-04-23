package com.janustech.helpsaap.model

data class PublishTypeModel(
    val publishTypeId: String,
    val publishTypeIdName: String
){
    override fun toString(): String {
        return publishTypeIdName
    }
}
