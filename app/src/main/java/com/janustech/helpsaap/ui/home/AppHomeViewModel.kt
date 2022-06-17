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
import com.janustech.helpsaap.network.requests.*
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
    var selectedFromDealDateTv = ""
    var selectedToDealDate = ""
    var selectedToDealDateTv = ""
    var selectedDealLocations = arrayListOf<String>()
    var dealOfDayImage = ""

    var editUserID = ""
    var editUsername = ""
    var editEmail = ""
    var editEditMob = ""
    var editPassword = ""
    var editProfImg = ""
    var addedCategories = arrayListOf<String>()
    var editLangId = ""
    var editOfferPercent = "0"

    var selectedFromAdsDate = ""
    var selectedToAdsDate = ""
    var adsImage = ""
    var selectedPublicLocationId = ""
    var selectedPublicLocationType = ""
    var selectedAMount = ""



    private val _locationListReceiver = MutableLiveData<Resource<ApiResponse<List<LocationListResponseData>>>>()
    val locationListReceiver: LiveData<Resource<ApiResponse<List<LocationListResponseData>>>>
        get() = _locationListReceiver

    val _postDealResponseReceiver = MutableLiveData<Resource<MultipartApiResponse>>()
    var postDealResponseReceiver: LiveData<Resource<MultipartApiResponse>>? = null
        get() = _postDealResponseReceiver

    private val _categoriesReceiver = MutableLiveData<Resource<ApiResponse<List<CategoryResponseData>>>>()
    val categoriesReceiver: LiveData<Resource<ApiResponse<List<CategoryResponseData>>>>
        get() = _categoriesReceiver

    var _editSubmitStatusReceiver = MutableLiveData<Resource<ApiResponse<String>>>()
    var editSubmitStatusReceiver: LiveData<Resource<ApiResponse<String>>>? = null
        get() = _editSubmitStatusReceiver

    var _publishAdsResponseReceiver = MutableLiveData<Resource<MultipartApiResponse>>()
    var publishAdsResponseReceiver: LiveData<Resource<MultipartApiResponse>>? = null
        get() = _publishAdsResponseReceiver

    private val _notificationsListReceiver = MutableLiveData<Resource<ApiResponse<List<NotificationResponseData>>>>()
    val notificationsListReceiver: LiveData<Resource<ApiResponse<List<NotificationResponseData>>>>
        get() = _notificationsListReceiver

    var _offerSubmitStatusReceiver = MutableLiveData<Resource<ApiResponse<CommonResponse>>>()
    var offerSubmitStatusReceiver: LiveData<Resource<ApiResponse<CommonResponse>>>? = null
        get() = _offerSubmitStatusReceiver

    var _editSubmitStatusReceiver_ = MutableLiveData<Resource<MultipartApiResponse>>()
    var editSubmitStatusReceiver_: LiveData<Resource<MultipartApiResponse>>? = null
        get() = _editSubmitStatusReceiver_

    private val _languageListReceiver = MutableLiveData<Resource<ApiResponse<List<LanguageListResponseData>>>>()
    val languageListReceiver: LiveData<Resource<ApiResponse<List<LanguageListResponseData>>>>
        get() = _languageListReceiver

    var _addCatgoriesResponseStatus = MutableLiveData<Resource<ApiResponse<String>>>()
    var addCatgoriesResponseStatus: LiveData<Resource<ApiResponse<String>>>? = null
        get() = _addCatgoriesResponseStatus

    private val _postedAdsListReceiver = MutableLiveData<Resource<ApiResponse<List<PostedAdsResponseData>>>>()
    val postedAdsListReceiver: LiveData<Resource<ApiResponse<List<PostedAdsResponseData>>>>
        get() = _postedAdsListReceiver

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
        editLangId = AppPreferences.userLanguageId
        editOfferPercent = userData?.offerpercentage?:"0"
    }

    private fun getUserObjectFromPreference(): UserData{
        val json = AppPreferences.userData
        return Gson().fromJson(json, UserData::class.java)
    }

    fun updateUserDataWithEditSuccess(){
        val data = UserData(
            userData?.userId?:"",
            editUsername,
            editEditMob,
            userData?.whatsapp?:"",
            userData?.email?:"",
            userData?.website?:"",
            userData?.currentLocation?:"",
            editProfImg,
            userData?.otp?:"",
            userData?.password?:"",
            userData?.offerpercentage?:"0",
            userData?.lat?:"",
            userData?.long?:"",
            userData?.areaname?:"",
            editLangId)
        AppPreferences.userData = Gson().toJson(data)
    }

    fun updateUserDataWithOfferPercentage(){
       val data = UserData(
            userData?.userId?:"",
            userData?.customerName?:"",
            userData?.phoneNumber?:"",
            userData?.whatsapp?:"",
            userData?.email?:"",
            userData?.website?:"",
            userData?.currentLocation?:"",
            userData?.photo?:"",
            userData?.otp?:"",
            userData?.password?:"",
            editOfferPercent,
            userData?.lat?:"",
            userData?.long?:"",
            userData?.areaname?:"",
            AppPreferences.userLanguageId)
        AppPreferences.userData = Gson().toJson(data)
    }

    private fun getUserNameIcon(): String{
        val name = if (userData?.businessname != null && userData?.businessname?.isNotEmpty() == true) {
            userData?.businessname
        } else {
            userData?.customerName?:"Unknown"
        }
        name?.let {
            val nameparts: List<String> = it.split(" ")
            var initFirst = nameparts[0][0].toString()
            var initSecond = ""
            if (nameparts.size > 1){
                initSecond = nameparts[1][0].toString()
            }
            return  initFirst + initSecond
        }?: run {
            return  "U"
        }
    }


    fun getLocationSuggestions(param: String){
        viewModelScope.launch {
            appIntroUseCase.getLocationList(LocationListRequest(param, AppPreferences.userLanguageId))
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
            try {
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
            } catch (e: Exception) {
                _postDealResponseReceiver.value = Resource.dataError("Something went wrong! Please check your inputs")
            }
        }
    }

    fun getCategories(param: String){
        viewModelScope.launch {
            appIntroUseCase.getCategories(CategoriesListRequest(param, AppPreferences.userLanguageId))
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
            try {
                val partCusId = MultiPartRequestHelper.createRequestBody("cus_id", userData?.userId?:"")
                val partStartDate = MultiPartRequestHelper.createRequestBody("start_date", selectedFromAdsDate)
                val partEndDate = MultiPartRequestHelper.createRequestBody("end_date", selectedToAdsDate)
                val partTxId = MultiPartRequestHelper.createRequestBody("transaction_id", ((100000..1000000).random()).toString())
                val partAmount = MultiPartRequestHelper.createRequestBody("amount", selectedAMount)
                val partAdsName = MultiPartRequestHelper.createRequestBody("ads_name", "nil")
                val partLocationType = MultiPartRequestHelper.createRequestBody("locationtype", selectedPublicLocationId)
                val partPublishLocId = MultiPartRequestHelper.createRequestBody("publish_loc", selectedPublicLocationId)
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
            } catch (e: Exception) {
                _publishAdsResponseReceiver.value = Resource.dataError("Something went wrong! Please check your inputs")
            }
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
                            editOfferPercent = percent
                            _offerSubmitStatusReceiver.value = apiResponse
                        }?: run {
                            _offerSubmitStatusReceiver.value = Resource.dataError("Invalid server response!")
                        }
                    } }
        }
    }



    fun editProfile(context: Context){
        viewModelScope.launch {

            try {
                val partCusId = MultiPartRequestHelper.createRequestBody("customer_id", editUserID)
                val partCusname = MultiPartRequestHelper.createRequestBody("cusname", editUsername)
                val partMob = MultiPartRequestHelper.createRequestBody("phone_number", editEditMob)
                val partLanguage = MultiPartRequestHelper.createRequestBody("language", editLangId)
                val partFile = MultiPartRequestHelper.createFileRequestBody(editProfImg, "image", context)

                homeUseCases.editProfile(
                    partCusId, partCusname, partMob, partLanguage, partFile
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
            } catch (e: Exception) {
                _editSubmitStatusReceiver_.value = Resource.dataError("Something went wrong! Please check your inputs")
            }

        }
    }

    fun getLanguages(){
        viewModelScope.launch {
            appIntroUseCase.getLanguages()
                .onStart { _languageListReceiver.value = Resource.loading() }
                .collect { apiResponse ->
                    apiResponse.let {
                        it.data?.let {
                            _languageListReceiver.value = apiResponse
                        }?: run {
                            _languageListReceiver.value = Resource.dataError("Invalid server response!")
                        }
                    }
                }

        }
    }

    fun addCategories(){
        val editProfileCategories = addedCategories.map { cat -> cat.toProfileCategoryModel() }
        val request = AddCategoriesRequest(editUserID, editProfileCategories)

        viewModelScope.launch {
            homeUseCases.addCategories(request)
                .onStart { _addCatgoriesResponseStatus.value =  Resource.loading()}
                .collect {  apiResponse ->
                    apiResponse.let{
                        it.data?.let{
                            _addCatgoriesResponseStatus.value = apiResponse
                        }?: run {
                            _addCatgoriesResponseStatus.value = Resource.dataError("Invalid server response!")
                        }
                    } }
        }
    }

    fun getPostedAds(){
        viewModelScope.launch {
            homeUseCases.getPostedAds(PostedListRequest(AppPreferences.userId))
                .onStart { _postedAdsListReceiver.value = Resource.loading() }
                .collect { apiResponse ->
                    apiResponse.let {
                        it.data?.let {
                            _postedAdsListReceiver.value = apiResponse
                        }?: run {
                            _postedAdsListReceiver.value = Resource.dataError("Invalid server response!")
                        }
                    }
                }

        }
    }




}