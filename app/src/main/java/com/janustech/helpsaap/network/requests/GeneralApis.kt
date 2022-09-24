package com.janustech.helpsaap.network.requests

import com.janustech.helpsaap.network.HttpEndPoints
import com.janustech.helpsaap.network.response.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GeneralApis {

    @GET(HttpEndPoints.LANGUAGES)
    suspend fun getLanguagesList(): ApiResponse<List<LanguageListResponseData>>

    @POST(HttpEndPoints.SEARCH_PLACES)
    suspend fun getLocationList(
        @Body locationListRequest: LocationListRequest
    ): ApiResponse<List<LocationListResponseData>>

    @POST(HttpEndPoints.DEALS_OF_DAY)
    suspend fun getDealsOfDay(
        @Body dealOfDayRequest: DealOfDayRequest
    ): ApiResponse<List<DealsOfDayResponseData>>

    @POST(HttpEndPoints.ADS_LIST)
    suspend fun getAdsList(
        @Body adsListRequest: AdsListRequest
    ): ApiResponse<List<AdsResponseData>>

    @POST(HttpEndPoints.CATEGORY_SEARCH)
    suspend fun getCategories(
        @Body categoriesListRequest: CategoriesListRequest
    ): ApiResponse<List<CategoryResponseData>>

    @POST(HttpEndPoints.COMPANIES)
    suspend fun getCompanyList(
        @Body companyListRequest: CompanyListRequest
    ): ApiResponse<List<CompanyResponseData>>

    @POST(HttpEndPoints.PROFILE_VIEW)
    suspend fun getProfileData(
        @Body profileDataRequest: ProfileDataRequest
    ): ApiResponse<List<ProfileViewResponseData>>

    @POST(HttpEndPoints.APP_VERSION)
    suspend fun getAppVersion(
        @Body appVersionRequest: AppVersionRequest
    ): ApiResponse<List<AppVersionResponse>>
}