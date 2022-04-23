package com.janustech.helpsaap.repositories

import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.EditProfileRequest
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.MultipartApiResponse
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
}