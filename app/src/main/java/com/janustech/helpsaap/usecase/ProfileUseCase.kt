package com.janustech.helpsaap.usecase

import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.LoginRequest
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.LoginResponseData
import com.janustech.helpsaap.network.response.MultipartApiResponse
import com.janustech.helpsaap.repositories.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject

class ProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend fun login(loginRequest: LoginRequest): Flow<Resource<ApiResponse<LoginResponseData>>> {
        return flow {
            profileRepository.login(loginRequest).collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun register(
        phonenumber: MultipartBody.Part,
        password: MultipartBody.Part,
        cusname: MultipartBody.Part,
        email: MultipartBody.Part,
        locationpinut: MultipartBody.Part,
        businessname: MultipartBody.Part,
        whatsapp: MultipartBody.Part,
        website: MultipartBody.Part,
        categoryid: MultipartBody.Part,
        transaction_id: MultipartBody.Part,
        amount: MultipartBody.Part,
        image: MultipartBody.Part
    ): Flow<Resource<MultipartApiResponse>> {
        return flow {
            profileRepository.register(
                phonenumber,
                password,
                cusname,
                email,
                locationpinut,
                businessname,
                whatsapp,
                website,
                categoryid,
                transaction_id,
                amount,
                image
            ).collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }
}