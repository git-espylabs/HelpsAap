package com.janustech.helpsaap.ui.startup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.janustech.helpsaap.model.UserData
import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.*
import com.janustech.helpsaap.network.response.*
import com.janustech.helpsaap.preference.AppPreferences
import com.janustech.helpsaap.usecase.AppIntroUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppIntroViewModel
@Inject constructor(
    private val appIntroUseCase: AppIntroUseCase): ViewModel() {

    var userLocationName = ""
    var userLocationId = ""
    var userLanguage = ""
    var userLanguageId = ""
    var userSelectedCategory = ""
    var userSelectedCategoryName = ""
    var userData: UserData? = null
    var userNameIc = ""
    var userName = ""

    private val _languageListReceiver = MutableLiveData<Resource<ApiResponse<List<LanguageListResponseData>>>>()
    val languageListReceiver: LiveData<Resource<ApiResponse<List<LanguageListResponseData>>>>
        get() = _languageListReceiver

    private val _locationListReceiver = MutableLiveData<Resource<ApiResponse<List<LocationListResponseData>>>>()
    val locationListReceiver: LiveData<Resource<ApiResponse<List<LocationListResponseData>>>>
        get() = _locationListReceiver

    private val _dealsOfDay = MutableLiveData<Resource<ApiResponse<List<DealsOfDayResponseData>>>>()
    val dealsOfDay: LiveData<Resource<ApiResponse<List<DealsOfDayResponseData>>>>
        get() = _dealsOfDay

    private val _adsListReceiver = MutableLiveData<Resource<ApiResponse<List<AdsResponseData>>>>()
    val adsListReceiver: LiveData<Resource<ApiResponse<List<AdsResponseData>>>>
        get() = _adsListReceiver

    val _categoriesReceiver = MutableLiveData<Resource<ApiResponse<List<CategoryResponseData>>>>()
    var categoriesReceiver: LiveData<Resource<ApiResponse<List<CategoryResponseData>>>>? = null
        get() = _categoriesReceiver

    private val _companyListReceiver = MutableLiveData<Resource<ApiResponse<List<CompanyResponseData>>>>()
    val companyListReceiver: LiveData<Resource<ApiResponse<List<CompanyResponseData>>>>
        get() = _companyListReceiver

    init {
        userLocationName = AppPreferences.userLocation
        userLocationId = AppPreferences.userLocationId
        userLanguage = AppPreferences.userLanguage
        userLanguageId = AppPreferences.userLanguageId
        if (AppPreferences.userId.isNotEmpty()) {
            userData = getUserObjectFromPreference()
            userNameIc = getUserNameIcon()
            userName = userData?.customerName?: ""
        }
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

    fun getDealsOfTheDay(catId: String){
        viewModelScope.launch {
            appIntroUseCase.getDealsOfDay(DealOfDayRequest(userLocationId, catId))
                .onStart { _dealsOfDay.value = Resource.loading() }
                .collect { apiResponse ->
                    apiResponse.let{
                        it.data?.let{
                            _dealsOfDay.value = apiResponse
                        }?: run {
                            _dealsOfDay.value = Resource.dataError("Invalid server response!")
                        }
                    }
                }
        }
    }

    fun getAdsList(catId: String){
        viewModelScope.launch {
            appIntroUseCase.getAdsList(AdsListRequest(userLocationId, catId))
                .onStart { _adsListReceiver.value = Resource.loading() }
                .collect { apiResponse ->
                    apiResponse.let{
                        it.data?.let{
                            _adsListReceiver.value = apiResponse
                        }?: run {
                            _adsListReceiver.value = Resource.dataError("Invalid server response!")
                        }
                    }
                }
        }
    }

    fun getCategories(param: String){
        viewModelScope.launch {
            appIntroUseCase.getCategories(CategoriesListRequest(param))
                .onStart {
                    _categoriesReceiver.value = Resource.loading()
                }
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

    fun getCompanies(){
        viewModelScope.launch {
            appIntroUseCase.getCompanyList(CompanyListRequest(userSelectedCategory))
                .onStart { _companyListReceiver.value = Resource.loading() }
                .collect { apiResponse ->
                    apiResponse.let{
                        it.data?.let{
                            _companyListReceiver.value = apiResponse
                        }?: run {
                            _companyListReceiver.value = Resource.dataError("Invalid server response!")
                        }
                    }
                }
        }
    }
}