package com.janustech.helpsaap.repositories

import com.janustech.helpsaap.network.HelpsAapApis
import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.response.MultipartApiResponse
import com.janustech.helpsaap.network.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody

class HomeRepositoryImpl(private val apiService: HelpsAapApis): HomeRepository {

    override fun postDeal(
        cus_id: MultipartBody.Part,
        start_date: MultipartBody.Part,
        enddate: MultipartBody.Part,
        locations: MultipartBody.Part,
        image: MultipartBody.Part
    ): Flow<Resource<MultipartApiResponse>> {
        return flow {
            emit(safeApiCall { apiService.postDeal(
                cus_id,
                start_date,
                enddate,
                locations,
                image
            ) })
        }.flowOn(Dispatchers.IO)
    }
}