package com.janustech.helpsaap.usecase

import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.*
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.LoginResponseData
import com.janustech.helpsaap.network.response.MultipartApiResponse
import com.janustech.helpsaap.network.response.SignupResponse
import com.janustech.helpsaap.repositories.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.Response
import okhttp3.ResponseBody
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
        latitude: MultipartBody.Part,
        longitube: MultipartBody.Part,
        areaname: MultipartBody.Part,
        language: MultipartBody.Part,
        offerpercentage: MultipartBody.Part,
        image: MultipartBody.Part
    ): Flow<Resource<ApiResponse<SignupResponse>>> {
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
                latitude,
                longitube,
                areaname,
                language,
                offerpercentage,
                image
            ).collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendOtp(otpSendRequest: OtpSendRequest): Flow<Resource<ApiResponse<String>>> {
        return flow {
            profileRepository.sendOtp(otpSendRequest).collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun verifyOtp(verifyOtpRequest: VerifyOtpRequest): Flow<Resource<ApiResponse<LoginResponseData>>> {
        return flow {
            profileRepository.verifyOtp(verifyOtpRequest).collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): Flow<Resource<ApiResponse<String>>> {
        return flow {
            profileRepository.resetPassword(resetPasswordRequest).collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun verifyMobile(verifyPhoneRequest: VerifyPhoneRequest): Flow<Resource<ApiResponse<String>>> {
        return flow {
            profileRepository.verifyMobile(verifyPhoneRequest).collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }
}