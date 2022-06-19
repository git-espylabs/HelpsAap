package com.janustech.helpsaap.repositories

import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.*
import com.janustech.helpsaap.network.response.*
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

    override fun getLocationList(locationListRequest: LocationListRequest): Flow<Resource<ApiResponse<List<LocationListResponseData>>>> {
        return flow {
            emit(safeApiCall { apiService.getLocationList(locationListRequest) })
        }.flowOn(Dispatchers.IO)
    }

    override fun getDealsOfDay(dealOfDayRequest: DealOfDayRequest): Flow<Resource<ApiResponse<List<DealsOfDayResponseData>>>> {
        return flow {
            emit(safeApiCall { apiService.getDealsOfDay(dealOfDayRequest) })
        }.flowOn(Dispatchers.IO)
    }

    override fun getAdsList(adsListRequest: AdsListRequest): Flow<Resource<ApiResponse<List<AdsResponseData>>>> {
        return flow {
            emit(safeApiCall { apiService.getAdsList(adsListRequest) })
        }.flowOn(Dispatchers.IO)
    }

    override fun getCategories(categoriesListRequest: CategoriesListRequest): Flow<Resource<ApiResponse<List<CategoryResponseData>>>> {
        return flow {
            emit(safeApiCall { apiService.getCategories(categoriesListRequest) })
        }.flowOn(Dispatchers.IO)
    }

    override fun getCompanyList(companyListRequest: CompanyListRequest): Flow<Resource<ApiResponse<List<CompanyResponseData>>>> {
        return flow {
            emit(safeApiCall { apiService.getCompanyList(companyListRequest) })
        }.flowOn(Dispatchers.IO)
    }

    override fun getProfileData(profileDataRequest: ProfileDataRequest): Flow<Resource<ApiResponse<List<ProfileViewResponseData>>>> {
        return flow {
            emit(safeApiCall { apiService.getProfileData(profileDataRequest) })
        }.flowOn(Dispatchers.IO)
    }
}