package com.janustech.helpsaap.repositories

import com.janustech.helpsaap.network.ProfileApis
import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.*
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.LoginResponseData
import com.janustech.helpsaap.network.response.MultipartApiResponse
import com.janustech.helpsaap.network.response.SignupResponse
import com.janustech.helpsaap.network.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.Response
import okhttp3.ResponseBody

class ProfileRepositoryImpl(private val apiService: ProfileApis): ProfileRepository {

    override fun login(loginRequest: LoginRequest): Flow<Resource<ApiResponse<LoginResponseData>>> {
        return flow {
            emit(safeApiCall { apiService.login(loginRequest) })
        }.flowOn(Dispatchers.IO)
    }

    override fun register(
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
            emit(safeApiCall { apiService.register(
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
            ) })
        }.flowOn(Dispatchers.IO)
    }

    override fun sendOtp(otpSendRequest: OtpSendRequest): Flow<Resource<ApiResponse<String>>> {
        return flow {
            emit(safeApiCall { apiService.sendOtp(otpSendRequest) })
        }.flowOn(Dispatchers.IO)
    }

    override fun verifyOtp(verifyOtpRequest: VerifyOtpRequest): Flow<Resource<ApiResponse<LoginResponseData>>> {
        return flow {
            emit(safeApiCall { apiService.verifyOtp(verifyOtpRequest) })
        }.flowOn(Dispatchers.IO)
    }

    override fun resetPassword(resetPasswordRequest: ResetPasswordRequest): Flow<Resource<ApiResponse<String>>> {
        return flow {
            emit(safeApiCall { apiService.resetPassword(resetPasswordRequest) })
        }.flowOn(Dispatchers.IO)
    }

    override fun verifyMobile(verifyPhoneRequest: VerifyPhoneRequest): Flow<Resource<ApiResponse<String>>> {
        return flow {
            emit(safeApiCall { apiService.verifyMobile(verifyPhoneRequest) })
        }.flowOn(Dispatchers.IO)
    }

}