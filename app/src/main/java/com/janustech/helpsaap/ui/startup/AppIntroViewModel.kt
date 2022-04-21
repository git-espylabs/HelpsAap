package com.janustech.helpsaap.ui.startup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.AdsListRequest
import com.janustech.helpsaap.network.requests.DealOfDayRequest
import com.janustech.helpsaap.network.requests.LocationListRequest
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

    val ACTION_NAVIGATE_TO_LOGIN = 1024
    val ACTION_NAVIGATE_TO_REGISTER = 1025

    var userLocationName = ""
    var userLocationId = ""
    var userLanguage = ""
    var userLanguageId = ""

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

    private val _categoriesReceiver = MutableLiveData<Resource<ApiResponse<List<CategoryResponseData>>>>()
    val categoriesReceiver: LiveData<Resource<ApiResponse<List<CategoryResponseData>>>>
        get() = _categoriesReceiver

    private val _loginResponseReceiver = MutableLiveData<Resource<ApiResponse<LoginResponseData>>>()
    val loginResponseReceiver: LiveData<Resource<ApiResponse<LoginResponseData>>>
        get() = _loginResponseReceiver

    init {
        userLocationName = AppPreferences.userLocation
        userLocationId = AppPreferences.userLocationId
        userLanguage = AppPreferences.userLanguage
        userLanguageId = AppPreferences.userLanguageId
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

    fun getDealsOfTheDay(){
        viewModelScope.launch {
            appIntroUseCase.getDealsOfDay(DealOfDayRequest(userLocationId))
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

    fun getAdsList(){
        viewModelScope.launch {
            appIntroUseCase.getAdsList(AdsListRequest(userLocationId))
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

    fun getCategories(){
        viewModelScope.launch {
            appIntroUseCase.getCategories()
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
}