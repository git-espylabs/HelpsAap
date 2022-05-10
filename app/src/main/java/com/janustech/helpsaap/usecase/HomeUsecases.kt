package com.janustech.helpsaap.usecase

import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.AddCategoriesRequest
import com.janustech.helpsaap.network.requests.AddOfferRequest
import com.janustech.helpsaap.network.requests.EditProfileRequest
import com.janustech.helpsaap.network.requests.PostedListRequest
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.MultipartApiResponse
import com.janustech.helpsaap.network.response.NotificationResponseData
import com.janustech.helpsaap.network.response.PostedAdsResponseData
import com.janustech.helpsaap.repositories.HomeRepository
import com.janustech.helpsaap.repositories.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject

class HomeUsecases@Inject constructor(
    private val homeRepository: HomeRepository
) {

    suspend fun postDeal(
        cus_id: MultipartBody.Part,
        start_date: MultipartBody.Part,
        enddate: MultipartBody.Part,
        locations: MultipartBody.Part,
        image: MultipartBody.Part
    ): Flow<Resource<MultipartApiResponse>>{
        return flow {
            homeRepository.postDeal(
                cus_id,
                start_date,
                enddate,
                locations,
                image
            ).collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun submitEditProfile(editProfileRequest: EditProfileRequest):Flow<Resource<ApiResponse<String>>>{
        return flow {
            homeRepository.submitEditProfile(editProfileRequest).collect { emit(it) }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postAds(
        cus_id: MultipartBody.Part,
        start_date: MultipartBody.Part,
        end_date: MultipartBody.Part,
        transaction_id: MultipartBody.Part,
        amount: MultipartBody.Part,
        ads_name: MultipartBody.Part,
        locationtype: MultipartBody.Part,
        publish_loc: MultipartBody.Part,
        image: MultipartBody.Part
    ): Flow<Resource<MultipartApiResponse>>{
        return flow {
            homeRepository.postAds(
                cus_id,
                start_date,
                end_date,
                transaction_id,
                amount,
                ads_name,
                locationtype,
                publish_loc,
                image
            ).collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getNotifications():Flow<Resource<ApiResponse<List<NotificationResponseData>>>>{
        return flow {
            homeRepository.getNotifications().collect { emit(it) }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun submitOffer(addOfferRequest: AddOfferRequest):Flow<Resource<ApiResponse<String>>>{
        return flow {
            homeRepository.submitOffer(addOfferRequest).collect { emit(it) }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun editProfile(
        customer_id: MultipartBody.Part,
        cusname: MultipartBody.Part,
        email: MultipartBody.Part,
        language: MultipartBody.Part,
        image: MultipartBody.Part
    ): Flow<Resource<MultipartApiResponse>> {
        return flow {
            homeRepository.editProfile(
                customer_id, cusname, email, language, image
            ).collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addCategories(addCategoriesRequest: AddCategoriesRequest):Flow<Resource<ApiResponse<String>>>{
        return flow {
            homeRepository.addCategories(addCategoriesRequest).collect { emit(it) }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPostedAds(postedListRequest: PostedListRequest):Flow<Resource<ApiResponse<List<PostedAdsResponseData>>>>{
        return flow {
            homeRepository.getPostedAds(postedListRequest).collect { emit(it) }
        }.flowOn(Dispatchers.IO)
    }

}