package com.janustech.helpsaap.network

import com.janustech.helpsaap.network.requests.*
import com.janustech.helpsaap.network.response.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface HelpsAapApis {

    @Multipart
    @POST(HttpEndPoints.DEAL_OF_DAY)
    suspend fun postDeal(
        @Part cus_id: MultipartBody.Part,
        @Part start_date: MultipartBody.Part,
        @Part enddate: MultipartBody.Part,
        @Part locations: MultipartBody.Part,
        @Part image: MultipartBody.Part
    ): MultipartApiResponse

    @POST(HttpEndPoints.EDIT_PROFILE)
    suspend fun submitEditProfile(
        @Body editProfileRequest: EditProfileRequest
    ): ApiResponse<String>

    @Multipart
    @POST(HttpEndPoints.ADD_ADS)
    suspend fun postAds(
        @Part cus_id: MultipartBody.Part,
        @Part start_date: MultipartBody.Part,
        @Part end_date: MultipartBody.Part,
        @Part transaction_id: MultipartBody.Part,
        @Part amount: MultipartBody.Part,
        @Part ads_name: MultipartBody.Part,
        @Part locationtype: MultipartBody.Part,
        @Part publish_loc: MultipartBody.Part,
        @Part image: MultipartBody.Part
    ): MultipartApiResponse

    @GET(HttpEndPoints.NOTIFICATIONS)
    suspend fun getNotifications(): ApiResponse<List<NotificationResponseData>>

    @POST(HttpEndPoints.ADD_OFFER)
    suspend fun submitOffer(
        @Body addOfferRequest: AddOfferRequest
    ): ApiResponse<CommonResponse>

    @Multipart
    @POST(HttpEndPoints.EDIT_PROFILE)
    suspend fun editProfile(
        @Part customer_id: MultipartBody.Part,
        @Part cusname: MultipartBody.Part,
        @Part phone_number: MultipartBody.Part,
        @Part language: MultipartBody.Part,
        @Part image: MultipartBody.Part
    ): MultipartApiResponse

    @POST(HttpEndPoints.ADDON_CATEGORIES)
    suspend fun addOnCategories(
        @Body addCategoriesRequest: AddCategoriesRequest
    ): ApiResponse<String>

    @POST(HttpEndPoints.POSTED_ADS)
    suspend fun getPostedAds(
        @Body postedListRequest: PostedListRequest
    ): ApiResponse<List<PostedAdsResponseData>>

    @GET(HttpEndPoints.ABOUT_US)
    suspend fun getAboutUs(
    ): ApiResponse<AboutUsResponse>

    @GET(HttpEndPoints.TNC)
    suspend fun getTNC(
    ): ApiResponse<TNCResponse>


}