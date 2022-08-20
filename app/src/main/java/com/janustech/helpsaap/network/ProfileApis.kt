package com.janustech.helpsaap.network

import com.janustech.helpsaap.network.requests.LoginRequest
import com.janustech.helpsaap.network.requests.OtpSendRequest
import com.janustech.helpsaap.network.requests.ResetPasswordRequest
import com.janustech.helpsaap.network.requests.VerifyOtpRequest
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.LoginResponseData
import com.janustech.helpsaap.network.response.MultipartApiResponse
import com.janustech.helpsaap.network.response.SignupResponse
import okhttp3.MultipartBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProfileApis {

    @POST(HttpEndPoints.LOGIN)
    suspend fun login(
        @Body loginRequest: LoginRequest,
    ): ApiResponse<LoginResponseData>

    @Multipart
    @POST(HttpEndPoints.REGISTER)
    suspend fun register(
        @Part phonenumber: MultipartBody.Part,
        @Part password: MultipartBody.Part,
        @Part cusname: MultipartBody.Part,
        @Part email: MultipartBody.Part,
        @Part locationpinut: MultipartBody.Part,
        @Part businessname: MultipartBody.Part,
        @Part whatsapp: MultipartBody.Part,
        @Part website: MultipartBody.Part,
        @Part categoryid: MultipartBody.Part,
        @Part transaction_id: MultipartBody.Part,
        @Part amount: MultipartBody.Part,
        @Part latitude: MultipartBody.Part,
        @Part longitube: MultipartBody.Part,
        @Part areaname: MultipartBody.Part,
        @Part language: MultipartBody.Part,
        @Part offerpercentage: MultipartBody.Part,
        @Part image: MultipartBody.Part
    ): ApiResponse<SignupResponse>

    @POST(HttpEndPoints.SEND_OTP)
    suspend fun sendOtp(
        @Body otpSendRequest: OtpSendRequest,
    ): ApiResponse<String>

    @POST(HttpEndPoints.VERIFY_OTP)
    suspend fun verifyOtp(
        @Body verifyOtpRequest: VerifyOtpRequest,
    ): ApiResponse<LoginResponseData>

    @POST(HttpEndPoints.RESET_PASSWORD)
    suspend fun resetPassword(
        @Body resetPasswordRequest: ResetPasswordRequest,
    ): ApiResponse<String>
}