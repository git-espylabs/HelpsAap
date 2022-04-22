package com.janustech.helpsaap.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.janustech.helpsaap.model.LocationDataModel
import com.janustech.helpsaap.model.UserData
import com.janustech.helpsaap.network.MultiPartRequestHelper
import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.requests.LocationListRequest
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.LocationListResponseData
import com.janustech.helpsaap.network.response.MultipartApiResponse
import com.janustech.helpsaap.preference.AppPreferences
import com.janustech.helpsaap.usecase.AppIntroUseCase
import com.janustech.helpsaap.usecase.HomeUsecases
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



    private val _locationListReceiver = MutableLiveData<Resource<ApiResponse<List<LocationListResponseData>>>>()
    val locationListReceiver: LiveData<Resource<ApiResponse<List<LocationListResponseData>>>>
        get() = _locationListReceiver

    private val _postDealResponseReceiver = MutableLiveData<Resource<MultipartApiResponse>>()
    val postDealResponseReceiver: LiveData<Resource<MultipartApiResponse>>
        get() = _postDealResponseReceiver

    init {
        userLocationName = AppPreferences.userLocation
        userLocationId = AppPreferences.userLocationId
        userData = getUserObjectFromPreference()
        userNameIc = getUserNameIcon()
        userName = userData?.customerName?: ""
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


}