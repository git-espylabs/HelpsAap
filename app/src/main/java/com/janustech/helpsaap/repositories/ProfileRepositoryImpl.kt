package com.janustech.helpsaap.repositories

import com.janustech.helpsaap.network.ProfileApis
import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.LoginRequest
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.LoginResponseData
import com.janustech.helpsaap.network.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ProfileRepositoryImpl(private val apiService: ProfileApis): ProfileRepository {

    override fun login(loginRequest: LoginRequest): Flow<Resource<ApiResponse<LoginResponseData>>> {
        return flow {
            emit(safeApiCall { apiService.login(loginRequest) })
        }.flowOn(Dispatchers.IO)
    }
}