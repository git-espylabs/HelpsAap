package com.janustech.helpsaap.repositories

import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.LoginRequest
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.LoginResponseData
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    fun login(loginDetails: LoginRequest): Flow<Resource<ApiResponse<LoginResponseData>>>
}