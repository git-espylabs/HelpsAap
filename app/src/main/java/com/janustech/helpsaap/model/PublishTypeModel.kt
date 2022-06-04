package com.janustech.helpsaap.model

data class PublishTypeModel(
    val publishTypeId: String,
    val publishTypeIdName: String,
    val packageList: List<AdsPackageModel>,
    var isSelected: Int = 0
){
    override fun toString(): String {
        return publishTypeIdName
    }
}
