package com.janustech.helpsaap.network

import com.janustech.helpsaap.network.requests.LoginRequest
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.LoginResponseData
import com.janustech.helpsaap.network.response.MultipartApiResponse
import okhttp3.MultipartBody
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
        @Part image: MultipartBody.Part
    ): MultipartApiResponse
}