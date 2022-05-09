package com.janustech.helpsaap.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.janustech.helpsaap.map.toProfileCategoryModel
import com.janustech.helpsaap.model.LocationDataModel
import com.janustech.helpsaap.model.UserData
import com.janustech.helpsaap.network.MultiPartRequestHelper
import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.AddOfferRequest
import com.janustech.helpsaap.network.requests.CategoriesListRequest
import com.janustech.helpsaap.network.requests.EditProfileRequest
import com.janustech.helpsaap.network.requests.LocationListRequest
import com.janustech.helpsaap.network.response.*
import com.janustech.helpsaap.preference.AppPreferences
import com.janustech.helpsaap.usecase.AppIntroUseCase
import com.janustech.helpsaap.usecase.HomeUsecases
import com.janustech.helpsaap.utils.CommonUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AppHomeViewModel @Inject constructor(private val appIntroUseCase: AppIntroUseCase,private val homeUseCases: HomeUsecases): ViewModel() {

    var userLocationName = ""
    var userLocationId = ""
    var userData: UserData? = null
    var userNameIc = ""
    var userName = ""

    var selectedFromDealDate = ""
    var selectedToDealDate = ""
    var selectedDealLocations = arrayListOf<String>()
    var dealOfDayImage = ""

    var editUserID = ""
    var editUsername = ""
    var editEmail = ""
    var editEditMob = ""
    var editPassword = ""
    var editProfImg = ""
    var addedCategories = arrayListOf<String>()

    var selectedFromAdsDate = ""
    var selectedToAdsDate = ""
    var adsImage = ""
    var selectedPublicLocationId = ""
    var selectedPublicLocationType = ""
    var selectedAMount = ""



    private val _locationListReceiver = MutableLiveData<Resource<ApiResponse<List<LocationListResponseData>>>>()
    val locationListReceiver: LiveData<Resource<ApiResponse<List<LocationListResponseData>>>>
        get() = _locationListReceiver

    private val _postDealResponseReceiver = MutableLiveData<Resource<MultipartApiResponse>>()
    val postDealResponseReceiver: LiveData<Resource<MultipartApiResponse>>
        get() = _postDealResponseReceiver

    private val _categoriesReceiver = MutableLiveData<Resource<ApiResponse<List<CategoryResponseData>>>>()
    val categoriesReceiver: LiveData<Resource<ApiResponse<List<CategoryResponseData>>>>
        get() = _categoriesReceiver

    private val _editSubmitStatusReceiver = MutableLiveData<Resource<ApiResponse<String>>>()
    val editSubmitStatusReceiver: LiveData<Resource<ApiResponse<String>>>
        get() = _editSubmitStatusReceiver

    private val _publishAdsResponseReceiver = MutableLiveData<Resource<MultipartApiResponse>>()
    val publishAdsResponseReceiver: LiveData<Resource<MultipartApiResponse>>
        get() = _publishAdsResponseReceiver

    private val _notificationsListReceiver = MutableLiveData<Resource<ApiResponse<List<NotificationResponseData>>>>()
    val notificationsListReceiver: LiveData<Resource<ApiResponse<List<NotificationResponseData>>>>
        get() = _notificationsListReceiver

    private val _offerSubmitStatusReceiver = MutableLiveData<Resource<ApiResponse<String>>>()
    val offerSubmitStatusReceiver: LiveData<Resource<ApiResponse<String>>>
        get() = _offerSubmitStatusReceiver

    private val _editSubmitStatusReceiver_ = MutableLiveData<Resource<MultipartApiResponse>>()
    val editSubmitStatusReceiver_: LiveData<Resource<MultipartApiResponse>>
        get() = _editSubmitStatusReceiver_

    init {
        userLocationName = AppPreferences.userLocation
        userLocationId = AppPreferences.userLocationId
        userData = getUserObjectFromPreference()
        userNameIc = getUserNameIcon()
        userName = userData?.customerName?: ""
        editUserID = userData?.userId?:""
        editUsername = userData?.customerName?: ""
        editEmail = userData?.email?: ""
        editEditMob = userData?.phoneNumber?: ""
        editProfImg = userData?.photo?:""
        editPassword = userData?.password?:""
    }

    private fun getUserObjectFromPreference(): UserData{
        val json = AppPreferences.userData
        return Gson().fromJson(json, UserData::class.java)
    }

    fun getUserNameIcon(): String{
        val name = userData?.customerName?:"Unknown"
        val nameparts: List<String> = name.split(" ")
        var initFirst = nameparts[0][0].toString()
        var initSecond = ""
        if (nameparts.size > 1){
            initSecond = nameparts[1][0].toString()
        }
        return  initFirst + initSecond
    }


    fun getLocationSuggestions(param: String){
        viewModelScope.launch {
            appIntroUseCase.getLocationList(LocationListRequest(param))
                .onStart { _locationListReceiver.value = Resource.loading() }
                .collect { apiResponse ->
                    apiResponse.let{
                        it.data?.let{
                            _locationListReceiver.value = apiResponse
                        }?: run {
                            _locationListReceiver.value = Resource.dataError("Invalid server response!")
                        }
                    }
                }
        }
    }

    fun postDeal(context: Context){
        viewModelScope.launch {
            val partCusId = MultiPartRequestHelper.createRequestBody("cus_id", userData?.userId?:"")
            val partStartDate = MultiPartRequestHelper.createRequestBody("start_date", selectedFromDealDate)
            val partEndDate = MultiPartRequestHelper.createRequestBody("enddate", selectedToDealDate)
            val partLocations = MultiPartRequestHelper.createRequestBody("locations", selectedDealLocations.joinToString(separator = ","))
            val partFile = MultiPartRequestHelper.createFileRequestBody(dealOfDayImage, "image", context)

            homeUseCases.postDeal(
                partCusId,
                partStartDate,
                partEndDate,
                partLocations,
                partFile
            )
                .onStart { _postDealResponseReceiver.value = Resource.loading() }
                .collect {  apiResponse ->
                    apiResponse.let {
                    it.data?.let { resp ->
                        if (resp.isResponseSuccess() && resp.data != null && resp.data.isNotEmpty()) {
                            _postDealResponseReceiver.value = apiResponse
                        }else if (resp.isResponseSuccess().not()){
                            _postDealResponseReceiver.value = Resource.dataError(resp.message)
                        }else{
                            _postDealResponseReceiver.value = Resource.dataError("Failed to post deal! Try again.")
                        }
                    }?: run {
                        _postDealResponseReceiver.value = Resource.dataError("Invalid server response!")
                    }
                } }
        }
    }

    fun getCategories(param: String){
        viewModelScope.launch {
            appIntroUseCase.getCategories(CategoriesListRequest(param))
                .onStart { _categoriesReceiver.value = Resource.loading() }
                .collect { apiResponse ->
                    apiResponse.let{
                        it.data?.let{
                            _categoriesReceiver.value = apiResponse
                        }?: run {
                            _categoriesReceiver.value = Resource.dataError("Invalid server response!")
                        }
                    }
                }
        }
    }

    fun submitProfileEdit(){
        val editProfileCategories = addedCategories.map { cat -> cat.toProfileCategoryModel() }
        val request = EditProfileRequest(editUserID, editUsername, editEmail, editProfileCategories)

        viewModelScope.launch {
            homeUseCases.submitEditProfile(request)
                .onStart { _editSubmitStatusReceiver.value =  Resource.loading()}
                .collect {  apiResponse ->
                    apiResponse.let{
                        it.data?.let{
                            _editSubmitStatusReceiver.value = apiResponse
                        }?: run {
                            _editSubmitStatusReceiver.value = Resource.dataError("Invalid server response!")
                        }
                    } }
        }
    }

    fun postAds(context: Context){
        viewModelScope.launch {
            val partCusId = MultiPartRequestHelper.createRequestBody("cus_id", userData?.userId?:"")
            val partStartDate = MultiPartRequestHelper.createRequestBody("start_date", selectedFromAdsDate)
            val partEndDate = MultiPartRequestHelper.createRequestBody("end_date", selectedToAdsDate)
            val partTxId = MultiPartRequestHelper.createRequestBody("transaction_id", ((100000..1000000).random()).toString())
            val partAmount = MultiPartRequestHelper.createRequestBody("amount", selectedAMount)
            val partAdsName = MultiPartRequestHelper.createRequestBody("ads_name", "")
            val partLocationType = MultiPartRequestHelper.createRequestBody("locationtype", selectedPublicLocationType)
            val partPublishLocId = MultiPartRequestHelper.createRequestBody("publish_loc", selectedPublicLocationType)
            val partFile = MultiPartRequestHelper.createFileRequestBody(adsImage, "image", context)

            homeUseCases.postAds(
                partCusId,
                partStartDate,
                partEndDate,
                partTxId,
                partAmount,
                partAdsName,
                partLocationType,
                partPublishLocId,
                partFile
            )
                .onStart { _publishAdsResponseReceiver.value = Resource.loading() }
                .collect {  apiResponse ->
                    apiResponse.let {
                        it.data?.let { resp ->
                            if (resp.isResponseSuccess() && resp.data != null && resp.data.isNotEmpty()) {
                                _publishAdsResponseReceiver.value = apiResponse
                            }else if (resp.isResponseSuccess().not()){
                                _publishAdsResponseReceiver.value = Resource.dataError(resp.message)
                            }else{
                                _publishAdsResponseReceiver.value = Resource.dataError("Failed to post deal! Try again.")
                            }
                        }?: run {
                            _publishAdsResponseReceiver.value = Resource.dataError("Invalid server response!")
                        }
                    } }
        }
    }


    fun getNotification(){
        viewModelScope.launch {
            homeUseCases.getNotifications()
                .onStart { _notificationsListReceiver.value = Resource.loading() }
                .collect { apiResponse ->
                    apiResponse.let{
                        it.data?.let{
                            _notificationsListReceiver.value = apiResponse
                        }?: run {
                            _notificationsListReceiver.value = Resource.dataError("Invalid server response!")
                        }
                    }
                }
        }
    }

    fun submitOffer(percent: String){
        val request = AddOfferRequest(AppPreferences.userId, percent)

        viewModelScope.launch {
            homeUseCases.submitOffer(request)
                .onStart { _offerSubmitStatusReceiver.value =  Resource.loading()}
                .collect {  apiResponse ->
                    apiResponse.let{
                        it.data?.let{
                            _offerSubmitStatusReceiver.value = apiResponse
                        }?: run {
                            _offerSubmitStatusReceiver.value = Resource.dataError("Invalid server response!")
                        }
                    } }
        }
    }



    fun editProfile(context: Context){
        viewModelScope.launch {

            val editProfileCategories = addedCategories.map { cat -> cat.toProfileCategoryModel() }
            val partCusId = MultiPartRequestHelper.createRequestBody("customer_id", editUserID)
            val partCusname = MultiPartRequestHelper.createRequestBody("cusname", editUsername)
            val partEmail = MultiPartRequestHelper.createRequestBody("email", editEmail)
            val partCategorylist = MultiPartRequestHelper.createRequestBody("categorylist", editProfileCategories.toString())
            val partFile = MultiPartRequestHelper.createFileRequestBody(editProfImg, "image", context)

            homeUseCases.editProfile(
                partCusId, partCusname, partEmail, partCategorylist, partFile
            )
                .onStart { _editSubmitStatusReceiver.value = Resource.loading() }
                .collect {  apiResponse ->
                    apiResponse.let {
                        it.data?.let { resp ->
                            CommonUtils.writeLogFile(context, "editProfile() -> Response: \n$resp")
                            if (resp.isResponseSuccess() && resp.data != null && resp.data.isNotEmpty()) {
                                CommonUtils.writeLogFile(context, "editProfile() -> Response: ResponseSuccess -> data:\n" + resp.data.toString())
                                AppPreferences.userImageDisk = editProfImg
                                _editSubmitStatusReceiver_.value = apiResponse
                            }else if (resp.isResponseSuccess().not()){
                                CommonUtils.writeLogFile(context, "editProfile() -> Response: ResponseFail:\n" + resp.message )
                                _editSubmitStatusReceiver.value = Resource.dataError(resp.message)
                            }else{
                                CommonUtils.writeLogFile(context, "editProfile() -> Response Error: unknown")
                                _editSubmitStatusReceiver.value = Resource.dataError("Failed to register! Try again.")
                            }
                        }?: run {
                            CommonUtils.writeLogFile(context, "editProfile() -> Response Null")
                            _editSubmitStatusReceiver_.value = Resource.dataError("Invalid server response!")
                        }
                    }
                }

        }
    }




}