package com.janustech.helpsaap.repositories

import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.*
import com.janustech.helpsaap.network.response.*
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

    fun submitOffer(addOfferRequest: AddOfferRequest):Flow<Resource<ApiResponse<CommonResponse>>>

    fun editProfile(
        customer_id: MultipartBody.Part,
        cusname: MultipartBody.Part,
        phone_number: MultipartBody.Part,
        language: MultipartBody.Part,
        image: MultipartBody.Part,
        businessname: MultipartBody.Part,
        whatsapp: MultipartBody.Part,
        website: MultipartBody.Part,
        areaname: MultipartBody.Part
    ): Flow<Resource<MultipartApiResponse>>

    fun addCategories(addCategoriesRequest: AddCategoriesRequest):Flow<Resource<ApiResponse<String>>>

    fun getPostedAds(postedListRequest: PostedListRequest):Flow<Resource<ApiResponse<List<PostedAdsResponseData>>>>

    fun getAbotUs():Flow<Resource<ApiResponse<AboutUsResponse>>>

    fun getTnc():Flow<Resource<ApiResponse<TNCResponse>>>

    fun getUserCategories(userCategoriesRequest: UserCategoriesRequest):Flow<Resource<ApiResponse<List<UserCategoriesResponse>>>>

    fun deleteUserCategory(deleteCategoryRequest: DeleteCategoryRequest):Flow<Resource<ApiResponse<String>>>

}