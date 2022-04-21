package com.janustech.helpsaap.network.requests

import com.janustech.helpsaap.network.response.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GeneralApis {

    @GET("langlist")
    suspend fun getLanguagesList(): ApiResponse<List<LanguageListResponseData>>

    @POST("searchplace")
    suspend fun getLocationList(
        @Body locationListRequest: LocationListRequest
    ): ApiResponse<List<LocationListResponseData>>

    @POST("dealsoftheday")
    suspend fun getDealsOfDay(
        @Body dealOfDayRequest: DealOfDayRequest
    ): ApiResponse<List<DealsOfDayResponseData>>

    @POST("adslist")
    suspend fun getAdsList(
        @Body adsListRequest: AdsListRequest
    ): ApiResponse<List<AdsResponseData>>

    @GET("categorylist")
    suspend fun getCategories(): ApiResponse<List<CategoryResponseData>>
}