package com.janustech.helpsaap.repositories

import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.LoginRequest
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.LanguageListResponseData
import com.janustech.helpsaap.network.response.LoginResponseData
import kotlinx.coroutines.flow.Flow

interface AppIntroRepository {

    fun getLanguages(): Flow<Resource<ApiResponse<List<LanguageListResponseData>>>>
}