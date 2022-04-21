package com.janustech.helpsaap.repositories

import com.janustech.helpsaap.network.ProfileApis
import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.LoginRequest
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.LoginResponseData
import com.janustech.helpsaap.network.response.MultipartApiResponse
import com.janustech.helpsaap.network.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody

class ProfileRepositoryImpl(private val apiService: ProfileApis): ProfileRepository {

    override fun login(loginRequest: LoginRequest): Flow<Resource<ApiResponse<LoginResponseData>>> {
        return flow {
            emit(safeApiCall { apiService.login(loginRequest) })
        }.flowOn(Dispatchers.IO)
    }

    override fun register(
        phonenumber: MultipartBody.Part,
        password: MultipartBody.Part,
        cusname: MultipartBody.Part,
        email: MultipartBody.Part,
        locationpinut: MultipartBody.Part,
        businessname: MultipartBody.Part,
        whatsapp: MultipartBody.Part,
        website: MultipartBody.Part,
        categoryid: MultipartBody.Part,
        transaction_id: MultipartBody.Part,
        amount: MultipartBody.Part,
        image: MultipartBody.Part
    ): Flow<Resource<MultipartApiResponse>> {
        return flow {
            emit(safeApiCall { apiService.register(
                phonenumber,
                password,
                cusname,
                email,
                locationpinut,
                businessname,
                whatsapp,
                website,
                categoryid,
                transaction_id,
                amount,
                image
            ) })
        }.flowOn(Dispatchers.IO)
    }
}