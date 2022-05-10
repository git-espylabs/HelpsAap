package com.janustech.helpsaap.network.requests

data class AddCategoriesRequest(
    val customerid: String,
    val addcategory: List<ProfileCategorySubmitRequest>
)