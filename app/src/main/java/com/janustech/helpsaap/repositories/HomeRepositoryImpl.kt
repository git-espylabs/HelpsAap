package com.janustech.helpsaap.repositories

import com.janustech.helpsaap.network.HelpsAapApis
import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.*
import com.janustech.helpsaap.network.response.*
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

    override fun submitEditProfile(editProfileRequest: EditProfileRequest): Flow<Resource<ApiResponse<String>>> {
        return flow {
            emit(safeApiCall { apiService.submitEditProfile(
                editProfileRequest
            ) })
        }.flowOn(Dispatchers.IO)
    }

    override fun postAds(
        cus_id: MultipartBody.Part,
        start_date: MultipartBody.Part,
        end_date: MultipartBody.Part,
        transaction_id: MultipartBody.Part,
        amount: MultipartBody.Part,
        ads_name: MultipartBody.Part,
        locationtype: MultipartBody.Part,
        publish_loc: MultipartBody.Part,
        image: MultipartBody.Part
    ): Flow<Resource<MultipartApiResponse>> {
        return flow {
            emit(safeApiCall { apiService.postAds(
                cus_id,
                start_date,
                end_date,
                transaction_id,
                amount,
                ads_name,
                locationtype,
                publish_loc,
                image
            ) })
        }.flowOn(Dispatchers.IO)
    }

    override fun getNotifications(): Flow<Resource<ApiResponse<List<NotificationResponseData>>>> {
        return flow {
            emit(safeApiCall { apiService.getNotifications() })
        }.flowOn(Dispatchers.IO)
    }

    override fun submitOffer(addOfferRequest: AddOfferRequest): Flow<Resource<ApiResponse<CommonResponse>>> {
        return flow {
            emit(safeApiCall { apiService.submitOffer(
                addOfferRequest
            ) })
        }.flowOn(Dispatchers.IO)
    }

    override fun editProfile(
        customer_id: MultipartBody.Part,
        cusname: MultipartBody.Part,
        phone_number: MultipartBody.Part,
        language: MultipartBody.Part,
        image: MultipartBody.Part
    ): Flow<Resource<MultipartApiResponse>> {
        return flow {
            emit(safeApiCall { apiService.editProfile(
                customer_id, cusname, phone_number, language, image
            ) })
        }.flowOn(Dispatchers.IO)
    }

    override fun addCategories(addCategoriesRequest: AddCategoriesRequest): Flow<Resource<ApiResponse<String>>> {
        return flow {
            emit(safeApiCall { apiService.addOnCategories(
                addCategoriesRequest
            ) })
        }.flowOn(Dispatchers.IO)
    }

    override fun getPostedAds(postedListRequest: PostedListRequest): Flow<Resource<ApiResponse<List<PostedAdsResponseData>>>> {
        return flow {
            emit(safeApiCall { apiService.getPostedAds(postedListRequest) })
        }.flowOn(Dispatchers.IO)
    }

    override fun getAbotUs(): Flow<Resource<ApiResponse<AboutUsResponse>>> {
        return flow {
            emit(safeApiCall { apiService.getAboutUs() })
        }.flowOn(Dispatchers.IO)
    }

    override fun getTnc(): Flow<Resource<ApiResponse<TNCResponse>>> {
        return flow {
            emit(safeApiCall { apiService.getTNC() })
        }.flowOn(Dispatchers.IO)
    }

    override fun getUserCategories(userCategoriesRequest: UserCategoriesRequest): Flow<Resource<ApiResponse<List<UserCategoriesResponse>>>> {
        return flow {
            emit(safeApiCall { apiService.getUserCategories(userCategoriesRequest) })
        }.flowOn(Dispatchers.IO)
    }

    override fun deleteUserCategory(deleteCategoryRequest: DeleteCategoryRequest): Flow<Resource<ApiResponse<String>>> {
        return flow {
            emit(safeApiCall {
                apiService.removeUserCategory(deleteCategoryRequest)
            })
        }.flowOn(Dispatchers.IO)
    }
}