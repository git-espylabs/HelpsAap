package com.janustech.helpsaap.repositories

import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.AddCategoriesRequest
import com.janustech.helpsaap.network.requests.AddOfferRequest
import com.janustech.helpsaap.network.requests.EditProfileRequest
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.MultipartApiResponse
import com.janustech.helpsaap.network.response.NotificationResponseData
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface HomeRepository {
    fun postDeal(
        cus_id: MultipartBody.Part,
        start_date: MultipartBody.Part,
        enddate: MultipartBody.Part,
        locations: MultipartBody.Part,
        image: MultipartBody.Part
    ): Flow<Resource<MultipartApiResponse>>

    fun submitEditProfile(editProfileRequest: EditProfileRequest):Flow<Resource<ApiResponse<String>>>

    fun postAds(
        cus_id: MultipartBody.Part,
        start_date: MultipartBody.Part,
        end_date: MultipartBody.Part,
        transaction_id: MultipartBody.Part,
        amount: MultipartBody.Part,
        ads_name: MultipartBody.Part,
        locationtype: MultipartBody.Part,
        publish_loc: MultipartBody.Part,
        image: MultipartBody.Part
    ): Flow<Resource<MultipartApiResponse>>

    fun getNotifications():Flow<Resource<ApiResponse<List<NotificationResponseData>>>>

    fun submitOffer(addOfferRequest: AddOfferRequest):Flow<Resource<ApiResponse<String>>>

    fun editProfile(
        customer_id: MultipartBody.Part,
        cusname: MultipartBody.Part,
        email: MultipartBody.Part,
        language: MultipartBody.Part,
        image: MultipartBody.Part
    ): Flow<Resource<MultipartApiResponse>>

    fun addCategories(addCategoriesRequest: AddCategoriesRequest):Flow<Resource<ApiResponse<String>>>

}