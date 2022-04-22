package com.janustech.helpsaap.usecase

import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.response.MultipartApiResponse
import com.janustech.helpsaap.repositories.HomeRepository
import com.janustech.helpsaap.repositories.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject

class HomeUsecases@Inject constructor(
    private val homeRepository: HomeRepository
) {

    suspend fun postDeal(
        cus_id: MultipartBody.Part,
        start_date: MultipartBody.Part,
        enddate: MultipartBody.Part,
        locations: MultipartBody.Part,
        image: MultipartBody.Part
    ): Flow<Resource<MultipartApiResponse>>{
        return flow {
            homeRepository.postDeal(
                cus_id,
                start_date,
                enddate,
                locations,
                image
            ).collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }
}