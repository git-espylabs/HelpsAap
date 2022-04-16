package com.janustech.helpsaap.network.requests

import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.LanguageListResponseData
import retrofit2.http.GET

interface GeneralApis {

    @GET("langlist")
    suspend fun getLanguagesList(): ApiResponse<List<LanguageListResponseData>>
}