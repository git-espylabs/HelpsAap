package com.janustech.helpsaap.repositories

import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.AdsListRequest
import com.janustech.helpsaap.network.requests.DealOfDayRequest
import com.janustech.helpsaap.network.requests.LocationListRequest
import com.janustech.helpsaap.network.requests.LoginRequest
import com.janustech.helpsaap.network.response.*
import kotlinx.coroutines.flow.Flow

interface AppIntroRepository {

    fun getLanguages(): Flow<Resource<ApiResponse<List<LanguageListResponseData>>>>

    fun getLocationList(locationListRequest: LocationListRequest): Flow<Resource<ApiResponse<List<LocationListResponseData>>>>

    fun getDealsOfDay(dealOfDayRequest: DealOfDayRequest): Flow<Resource<ApiResponse<List<DealsOfDayResponseData>>>>

    fun getAdsList(adsListRequest: AdsListRequest): Flow<Resource<ApiResponse<List<AdsResponseData>>>>

    fun getCategories(): Flow<Resource<ApiResponse<List<CategoryResponseData>>>>
}