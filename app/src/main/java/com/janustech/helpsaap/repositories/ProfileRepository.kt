package com.janustech.helpsaap.repositories

import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.LoginRequest
import com.janustech.helpsaap.network.requests.OtpSendRequest
import com.janustech.helpsaap.network.requests.ResetPasswordRequest
import com.janustech.helpsaap.network.requests.VerifyOtpRequest
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.LoginResponseData
import com.janustech.helpsaap.network.response.MultipartApiResponse
import com.janustech.helpsaap.network.response.SignupResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.http.Part

interface ProfileRepository {

    fun login(loginDetails: LoginRequest): Flow<Resource<ApiResponse<LoginResponseData>>>

    fun register(
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
    ): Flow<Resource<ApiResponse<SignupResponse>>>

    fun sendOtp(otpSendRequest: OtpSendRequest): Flow<Resource<ApiResponse<String>>>

    fun verifyOtp(verifyOtpRequest: VerifyOtpRequest): Flow<Resource<ApiResponse<LoginResponseData>>>

    fun resetPassword(resetPasswordRequest: ResetPasswordRequest): Flow<Resource<ApiResponse<String>>>
}