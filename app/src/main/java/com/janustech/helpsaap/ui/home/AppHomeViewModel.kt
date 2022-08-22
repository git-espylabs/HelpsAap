package com.janustech.helpsaap.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.janustech.helpsaap.map.toProfileCategoryModel
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
    var editLangName = ""
    var editOfferPercent = "0"

    var selectedFromAdsDate = ""
    var selectedToAdsDate = ""
    var adsImage = ""
    var selectedPublicLocationId = ""
    var selectedPublicLocationType = ""
    var selectedAMount = ""

    //
    var edtBusinessName=""
    var edtWhatsapp=""
    var edtWebsite=""
    var edtAreaName=""
    var edtUserLat=""
    var edtUserLon=""



    private val _locationListReceiver = MutableLiveData<Resource<ApiResponse<List<LocationListResponseData>>>>()
    val locationListReceiver: LiveData<Resource<ApiResponse<List<LocationListResponseData>>>>
        get() = _locationListReceiver

    val _postDealResponseReceiver = MutableLiveData<Resource<MultipartApiResponse>>()
    var postDealResponseReceiver: LiveData<Resource<MultipartApiResponse>>? = null
        get() = _postDealResponseReceiver

    var _categoriesReceiver = MutableLiveData<Resource<ApiResponse<List<CategoryResponseData>>>>()
    var categoriesReceiver: LiveData<Resource<ApiResponse<List<CategoryResponseData>>>>? = null
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

    var _postedAdsListReceiver = MutableLiveData<Resource<ApiResponse<List<PostedAdsResponseData>>>>()
    var postedAdsListReceiver: LiveData<Resource<ApiResponse<List<PostedAdsResponseData>>>>? = null
        get() = _postedAdsListReceiver

    private var _aboutUsRespReceiver = MutableLiveData<Resource<ApiResponse<AboutUsResponse>>>()
    val aboutUsRespReceiver: LiveData<Resource<ApiResponse<AboutUsResponse>>>
        get() = _aboutUsRespReceiver

    var _tncRespReceiver = MutableLiveData<Resource<ApiResponse<TNCResponse>>>()
    var tncRespReceiver: LiveData<Resource<ApiResponse<TNCResponse>>>? = null
        get() = _tncRespReceiver

    var _userCatsListReceiver = MutableLiveData<Resource<ApiResponse<List<UserCategoriesResponse>>>>()
    var userCatsListReceiver: LiveData<Resource<ApiResponse<List<UserCategoriesResponse>>>>? = null
        get() = _userCatsListReceiver

    var _userCatRemoveReceiver = MutableLiveData<Resource<ApiResponse<String>>>()
    var userCatRemoveReceiver: LiveData<Resource<ApiResponse<String>>>? = null
        get() = _userCatRemoveReceiver


    var _userMapLocationUpdateResponseReceiver = MutableLiveData<Resource<ApiResponse<String>>>()
    var userMapLocationUpdateResponseReceiver: LiveData<Resource<ApiResponse<String>>>? = null
        get() = _userMapLocationUpdateResponseReceiver

    init {
        userLocationName = AppPreferences.userLocation
        userLocationId = AppPreferences.userLocationId
        userData = getUserObjectFromPreference()
        userNameIc = getUserNameIcon()
        userData?.apply {
            if (customerName.isNullOrEmpty().not()){
                userName = customerName
                editUsername = customerName
            }
            if (userId.isNullOrEmpty().not()){
                editUserID = userId
            }
            if (email.isNullOrEmpty().not()){
                editEmail = email
            }
            if (phoneNumber.isNullOrEmpty().not()){
                editEditMob = phoneNumber
            }
            if (photo.isNullOrEmpty().not()){
                editProfImg = photo
            }
            if (password.isNullOrEmpty().not()){
                editPassword = password
            }
            if (offerpercentage.isNullOrEmpty().not()){
                editOfferPercent = offerpercentage
            }
            if (lat.isNullOrEmpty().not()){
                edtUserLat = lat
            }
            if (long.isNullOrEmpty().not()){
                edtUserLon = long
            }
            if (businessname.isNullOrEmpty().not()){
                edtBusinessName = businessname
            }
            if (whatsapp.isNullOrEmpty().not()){
                edtWhatsapp = whatsapp
            }
            if (website.isNullOrEmpty().not()){
                edtWebsite = website
            }
            if (areaname.isNullOrEmpty().not()){
                edtAreaName = areaname
            }
        }

        editLangId = AppPreferences.userLanguageId

    }

    private fun getUserObjectFromPreference(): UserData{
        val json = AppPreferences.userData
        return Gson().fromJson(json, UserData::class.java)
    }

    /*fun updateUserDataWithEditSuccess(){
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
    }*/

    fun updateUserDataWithEditSuccess(){
        val data = UserData(
            userData?.userId?:"",
            editUsername,
            editEditMob,
            edtWhatsapp,
            editEmail,
            edtWebsite,
            userData?.currentLocation?:"",
            editProfImg,
            userData?.otp?:"",
            userData?.password?:"",
            userData?.offerpercentage?:"0",
            userData?.lat?:"",
            userData?.long?:"",
            edtAreaName,
            editLangId,edtBusinessName)
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

    fun updateUserDataWithLatLon(){
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
            userData?.offerpercentage?:"0",
            edtUserLat,
            edtUserLon,
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
                val partLocationType = MultiPartRequestHelper.createRequestBody("locationtype", selectedPublicLocationType)
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
                var isImgEdited = "0"
                isImgEdited = if (editProfImg != (userData?.photo ?: "")) {
                    "1"
                } else {
                    "0"
                }
                val imFag = MultiPartRequestHelper.createRequestBody("imFag", isImgEdited)
                val partFilePair = MultiPartRequestHelper.createFileRequestBodyN(editProfImg, "image", context)
                val partFile = partFilePair.first

                //new fields
                val businessname = MultiPartRequestHelper.createRequestBody("businessname", edtBusinessName)
                val whatsapp = MultiPartRequestHelper.createRequestBody("whatsapp", edtWhatsapp)
                val website = MultiPartRequestHelper.createRequestBody("website", edtWebsite)
                val areaname = MultiPartRequestHelper.createRequestBody("areaname", edtAreaName)


                homeUseCases.editProfile(
                    partCusId, partCusname, partMob, partLanguage, partFile,businessname,whatsapp,website,areaname,imFag
                )
                    .onStart { _editSubmitStatusReceiver.value = Resource.loading() }
                    .collect {  apiResponse ->
                        apiResponse.let {
                            it.data?.let { resp ->
                                CommonUtils.writeLogFile(context, "editProfile() -> Response: \n$resp")
                                if (resp.isResponseSuccess() && resp.data != null && resp.data.isNotEmpty()) {
                                    CommonUtils.writeLogFile(context, "editProfile() -> Response: ResponseSuccess -> data:\n" + resp.data.toString())
                                    AppPreferences.userImageDisk = editProfImg
                                    if (editProfImg != (userData?.photo ?: "")) {
                                        editProfImg = partFilePair.second
                                    }
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

    fun getAboutUs(){
        viewModelScope.launch {
            homeUseCases.getAboutUs()
                .onStart { _aboutUsRespReceiver.value = Resource.loading() }
                .collect { apiResponse ->
                    apiResponse.let {
                        it.data?.let {
                            _aboutUsRespReceiver.value = apiResponse
                        }?: run {
                            _aboutUsRespReceiver.value = Resource.dataError("Invalid server response!")
                        }
                    }
                }

        }
    }

    fun getTnc(){
        viewModelScope.launch {
            homeUseCases.getTnc()
                .onStart { _tncRespReceiver.value = Resource.loading() }
                .collect { apiResponse ->
                    apiResponse.let {
                        it.data?.let {
                            _tncRespReceiver.value = apiResponse
                        }?: run {
                            _tncRespReceiver.value = Resource.dataError("Invalid server response!")
                        }
                    }
                }

        }
    }

    fun getUserCategories(){
        viewModelScope.launch {
            homeUseCases.getUserCategories(UserCategoriesRequest(AppPreferences.userId))
                .onStart { _userCatsListReceiver.value = Resource.loading() }
                .collect { apiResponse ->
                    apiResponse.let {
                        it.data?.let {
                            _userCatsListReceiver.value = apiResponse
                        }?: run {
                            _userCatsListReceiver.value = Resource.dataError("Invalid server response!")
                        }
                    }
                }

        }
    }

    fun removeUserCat(catid: String){
        viewModelScope.launch {
            homeUseCases.removeCategory(DeleteCategoryRequest(catid))
                .onStart { _userCatRemoveReceiver.value = Resource.loading() }
                .collect { apiResponse ->
                    apiResponse.let {
                        it.data?.let {
                            _userCatRemoveReceiver.value = apiResponse
                        }?: run {
                            _userCatRemoveReceiver.value = Resource.dataError("Invalid server response!")
                        }
                    }
                }

        }
    }

    fun updateUserLocationFromMap(){
        viewModelScope.launch {
            homeUseCases.updateUserLocationFromMap(UpdateMapLocationRequest(AppPreferences.userId, edtUserLat, edtUserLon))
                .onStart { _userMapLocationUpdateResponseReceiver.value = Resource.loading() }
                .collect { apiResponse ->
                    apiResponse.let {
                        it.data?.let {
                            _userMapLocationUpdateResponseReceiver.value = apiResponse
                        }?: run {
                            _userMapLocationUpdateResponseReceiver.value = Resource.dataError("Invalid server response!")
                        }
                    }
                }
        }
    }




}