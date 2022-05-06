package com.janustech.helpsaap.repositories

import com.janustech.helpsaap.network.HelpsAapApis
import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.AddOfferRequest
import com.janustech.helpsaap.network.requests.EditProfileRequest
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.MultipartApiResponse
import com.janustech.helpsaap.network.response.NotificationResponseData
import com.janustech.helpsaap.network.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody

class HomeRepositoryImpl(private val apiService: HelpsAapApis): HomeRepository {

    override fun postDeal(
        cus_id: MultipartBody.Part,
        start_date: MultipartBody.Part,
        enddate: MultipartBody.Part,
        locations: MultipartBody.Part,
        image: MultipartBody.Part
    ): Flow<Resource<MultipartApiResponse>> {
        return flow {
            emit(safeApiCall { apiService.postDeal(
                cus_id,
                start_date,
                enddate,
                locations,
                image
            ) })
        }.flowOn(Dispatchers.IO)
    }

    override fun submitEditProfile(editProfileRequest: EditProfileRequest): Flow<Resource<ApiResponse<String>>> {
        return flow {
            emit(safeApiCall { apiService.submitEditProfile(
                editProfileRequest
            ) })
        }.flowOn(Dispatchers.IO)
    }

    override fun postAds(
        cus_id: MultipartBody.Part,
        start_date: MultipartBody.Part,
        end_date: MultipartBody.Part,
        transaction_id: MultipartBody.Part,
        amount: MultipartBody.Part,
        ads_name: MultipartBody.Part,
        locationtype: MultipartBody.Part,
        publish_loc: MultipartBody.Part,
        image: MultipartBody.Part
    ): Flow<Resource<MultipartApiResponse>> {
        return flow {
            emit(safeApiCall { apiService.postAds(
                cus_id,
                start_date,
                end_date,
                transaction_id,
                amount,
                ads_name,
                locationtype,
                publish_loc,
                image
            ) })
        }.flowOn(Dispatchers.IO)
    }

    override fun getNotifications(): Flow<Resource<ApiResponse<List<NotificationResponseData>>>> {
        return flow {
            emit(safeApiCall { apiService.getNotifications() })
        }.flowOn(Dispatchers.IO)
    }

    override fun submitOffer(addOfferRequest: AddOfferRequest): Flow<Resource<ApiResponse<String>>> {
        return flow {
            emit(safeApiCall { apiService.submitOffer(
                addOfferRequest
            ) })
        }.flowOn(Dispatchers.IO)
    }
}