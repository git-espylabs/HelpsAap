package com.janustech.helpsaap.model

data class LocationDataModel(
    val id: String = "",
    val panchayath: String = "",
    val district: String = "",
    val state: String = ""
){
    override fun toString(): String {
        return "$panchayath, $district"
    }
}
