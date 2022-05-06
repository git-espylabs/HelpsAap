package com.janustech.helpsaap.ui.profile

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janustech.helpsaap.BuildConfig
import com.janustech.helpsaap.network.MultiPartRequestHelper
import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.network.requests.CategoriesListRequest
import com.janustech.helpsaap.network.requests.LocationListRequest
import com.janustech.helpsaap.network.requests.LoginRequest
import com.janustech.helpsaap.network.response.*
import com.janustech.helpsaap.usecase.AppIntroUseCase
import com.janustech.helpsaap.usecase.ProfileUseCase
import com.janustech.helpsaap.utils.CommonUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUseCase: ProfileUseCase,
    private val appIntroUseCase: AppIntroUseCase
): ViewModel() {

    var userName = ""
    var password = ""

    var regName = ""
    var regMob = ""
    var regPass = ""
    var regPin = ""
    var regCmpny = ""
    var regWhatsapNo = ""
    var regEmail = ""
    var regWeb = ""
    var regCategoryId = ""
    var regTxId = ""
    var regAmount = ""
    var regImage = ""
    var regLatitude = "0.0"
    var regLongitude = "0.0"
    var regLocalArea = ""

    private val _loginResponseReceiver = MutableLiveData<Resource<ApiResponse<LoginResponseData>>>()
    val loginResponseReceiver: LiveData<Resource<ApiResponse<LoginResponseData>>>
        get() = _loginResponseReceiver

    private val _registerResponseReceiver = MutableLiveData<Resource<MultipartApiResponse>>()
    val registerResponseReceiver: LiveData<Resource<MultipartApiResponse>>
            get() = _registerResponseReceiver

    private val _categoriesReceiver = MutableLiveData<Resource<ApiResponse<List<CategoryResponseData>>>>()
    val categoriesReceiver: LiveData<Resource<ApiResponse<List<CategoryResponseData>>>>
        get() = _categoriesReceiver

    private val _locationListReceiver = MutableLiveData<Resource<ApiResponse<List<LocationListResponseData>>>>()
    val locationListReceiver: LiveData<Resource<ApiResponse<List<LocationListResponseData>>>>
        get() = _locationListReceiver

    init {
        if (BuildConfig.DEBUG){
            userName = "tesjo@gmail.com"
            password = "qwerty"
        }
    }

    fun processLogin(){
        loginApp(LoginRequest(userName, password))
    }

    private fun loginApp(loginRequest: LoginRequest){
        viewModelScope.launch {
            profileUseCase.login(loginRequest).onStart {
                _loginResponseReceiver.value = Resource.loading()
            }.collect { apiResponse ->
                apiResponse.let {
                    it.data?.let { resp ->
                        if (resp.isResponseSuccess() && resp.data != null) {
                            _loginResponseReceiver.value = apiResponse
                        } else if (resp.isResponseSuccess().not()) {
                            _loginResponseReceiver.value = Resource.dataError(resp.message)
                        }else{
                            _loginResponseReceiver.value = Resource.dataError("Failed to Login! Try again.")
                        }
                    }?: run {
                        _loginResponseReceiver.value = Resource.dataError("Invalid server response!")
                    }
                }

            }
        }
    }

    fun registerApp(context: Context){
        CommonUtils.writeLogFile(context, "registerApp() -> Request Data ->\n" +
                "phonenumber: " + regMob + "\n" +
                "password: " + regMob + "\n" +
                "cusname: " + regMob + "\n" +
                "email: " + regMob + "\n" +
                "locationpinut: " + regMob + "\n" +
                "businessname: " + regMob + "\n" +
                "whatsapp: " + regMob + "\n" +
                "website: " + regMob + "\n" +
                "categoryid: " + regMob + "\n" +
                "image: " + regMob + "\n"
        )
        viewModelScope.launch {
            val partPhone = MultiPartRequestHelper.createRequestBody("phonenumber", regMob)
            val partPassword = MultiPartRequestHelper.createRequestBody("password", regPass)
            val partCusname = MultiPartRequestHelper.createRequestBody("cusname", regName)
            val partEmail = MultiPartRequestHelper.createRequestBody("email", regEmail)
            val partLocationPinut = MultiPartRequestHelper.createRequestBody("locationpinut", regPin)
            val partBusinessNname= MultiPartRequestHelper.createRequestBody("businessname", regCmpny)
            val partWhatsapp = MultiPartRequestHelper.createRequestBody("whatsapp", regWhatsapNo)
            val partWebsite = MultiPartRequestHelper.createRequestBody("website", regWeb)
            val partCategoryid = MultiPartRequestHelper.createRequestBody("categoryid", regCategoryId)
            val partTransactionId = MultiPartRequestHelper.createRequestBody("transaction_id", ((100000..1000000).random()).toString())
            val partAmount = MultiPartRequestHelper.createRequestBody("amount", "99")
            val partFile = MultiPartRequestHelper.createFileRequestBody(regImage, "image", context)

            profileUseCase.register(
                partPhone,
                partPassword,
                partCusname,
                partEmail,
                partLocationPinut,
                partBusinessNname,
                partWhatsapp,
                partWebsite,
                partCategoryid,
                partTransactionId,
                partAmount,
                partFile
            )
                .onStart { _registerResponseReceiver.value = Resource.loading() }
                .collect {  apiResponse ->
                    apiResponse.let {
                        it.data?.let { resp ->
                            CommonUtils.writeLogFile(context, "registerApp() -> Response: \n$resp")
                            if (resp.isResponseSuccess() && resp.data != null && resp.data.isNotEmpty()) {
                                CommonUtils.writeLogFile(context, "registerApp() -> Response: ResponseSuccess -> data:\n" + resp.data.toString())
                                _registerResponseReceiver.value = apiResponse
                            }else if (resp.isResponseSuccess().not()){
                                CommonUtils.writeLogFile(context, "registerApp() -> Response: ResponseFail:\n" + resp.message )
                                _registerResponseReceiver.value = Resource.dataError(resp.message)
                            }else{
                                CommonUtils.writeLogFile(context, "registerApp() -> Response Error: unknown")
                                _registerResponseReceiver.value = Resource.dataError("Failed to register! Try again.")
                            }
                        }?: run {
                            CommonUtils.writeLogFile(context, "registerApp() -> Response Null")
                            _registerResponseReceiver.value = Resource.dataError("Invalid server response!")
                        }
                    }
                }

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
}