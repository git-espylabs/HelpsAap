package com.janustech.helpsaap.network

import com.janustech.helpsaap.network.response.MultipartApiResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface HelpsAapApis {

    @Multipart
    @POST("dealoftheday")
    suspend fun postDeal(
        @Part cus_id: MultipartBody.Part,
        @Part start_date: MultipartBody.Part,
        @Part enddate: MultipartBody.Part,
        @Part locations: MultipartBody.Part,
        @Part image: MultipartBody.Part
    ): MultipartApiResponse
}