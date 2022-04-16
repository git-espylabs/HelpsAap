package com.janustech.helpsaap.repositories

import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.GeneralApis
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.LanguageListResponseData
import com.janustech.helpsaap.network.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AppIntroRepositoryImpl(private val apiService: GeneralApis): AppIntroRepository {
    override fun getLanguages(): Flow<Resource<ApiResponse<List<LanguageListResponseData>>>> {
        return flow {
            emit(safeApiCall { apiService.getLanguagesList() })
        }.flowOn(Dispatchers.IO)
    }
}