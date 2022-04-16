package com.janustech.helpsaap.usecase

import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.LanguageListResponseData
import com.janustech.helpsaap.repositories.AppIntroRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AppIntroUseCase @Inject constructor(
    private val appIntroRepository: AppIntroRepository
) {
    suspend fun getLanguages(): Flow<Resource<ApiResponse<List<LanguageListResponseData>>>> {
        return flow {
            appIntroRepository.getLanguages().collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }
}