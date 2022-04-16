package com.janustech.helpsaap.usecase

import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.LoginRequest
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.LoginResponseData
import com.janustech.helpsaap.repositories.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
}