package com.janustech.helpsaap.usecase

import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.*
import com.janustech.helpsaap.network.response.*
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

    suspend fun getLocationList(locationListRequest: LocationListRequest): Flow<Resource<ApiResponse<List<LocationListResponseData>>>> {
        return flow {
            appIntroRepository.getLocationList(locationListRequest).collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getDealsOfDay(dealOfDayRequest: DealOfDayRequest): Flow<Resource<ApiResponse<List<DealsOfDayResponseData>>>> {
        return flow {
            appIntroRepository.getDealsOfDay(dealOfDayRequest).collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAdsList(adsListRequest: AdsListRequest): Flow<Resource<ApiResponse<List<AdsResponseData>>>> {
        return flow {
            appIntroRepository.getAdsList(adsListRequest).collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCategories(categoriesListRequest: CategoriesListRequest): Flow<Resource<ApiResponse<List<CategoryResponseData>>>> {
        return flow {
            appIntroRepository.getCategories(categoriesListRequest).collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCompanyList(companyListRequest: CompanyListRequest): Flow<Resource<ApiResponse<List<CompanyResponseData>>>> {
        return flow {
            appIntroRepository.getCompanyList(companyListRequest).collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getProfileData(profileDataRequest: ProfileDataRequest): Flow<Resource<ApiResponse<List<ProfileViewResponseData>>>> {
        return flow {
            appIntroRepository.getProfileData(profileDataRequest).collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }
}