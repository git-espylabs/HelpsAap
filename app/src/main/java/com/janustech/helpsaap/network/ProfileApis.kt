package com.janustech.helpsaap.network

import com.janustech.helpsaap.network.requests.LoginRequest
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.LoginResponseData
import retrofit2.http.Body
import retrofit2.http.POST

interface ProfileApis {

    @POST("login")
    suspend fun login(
        @Body loginRequest: LoginRequest,
    ): ApiResponse<LoginResponseData>
}