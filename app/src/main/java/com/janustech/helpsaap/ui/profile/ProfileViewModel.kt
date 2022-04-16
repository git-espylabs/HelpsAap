package com.janustech.helpsaap.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janustech.helpsaap.BuildConfig
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.network.requests.LoginRequest
import com.janustech.helpsaap.network.response.LoginResponseData
import com.janustech.helpsaap.usecase.ProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val profileUseCase: ProfileUseCase): ViewModel() {

    var userName = ""
    var password = ""

    private val _loginResponseReceiver = MutableLiveData<Pair<Status, Any?>>()
    val loginResponseReceiver: MutableLiveData<Pair<Status, Any?>>
            get() = _loginResponseReceiver

    init {
        if (BuildConfig.DEBUG){
            userName = "gopikaespylabs@gmail.com"
            password = "gopika@123"
        }
    }

    fun processLogin(){
        loginApp(LoginRequest(userName, password))
    }

    private fun loginApp(loginRequest: LoginRequest){
        viewModelScope.launch {
            profileUseCase.login(loginRequest).onStart {
                _loginResponseReceiver.value = Pair(Status.LOADING, null)
            }.collect { loginResponse ->
                loginResponse.let {
                    when(it.status){
                        Status.SUCCESS ->{
                            it.data?.let { responseData ->
                                if (responseData.isResponseSuccess()){
                                    _loginResponseReceiver.value = Pair(Status.SUCCESS, responseData.data)
                                }else{
                                    _loginResponseReceiver.value = Pair(Status.SUCCESS, responseData.message)
                                }
                            }?: run {
                                _loginResponseReceiver.value = Pair(Status.SUCCESS, "Invalid server response!")
                            }
                        }
                        else ->{
                            _loginResponseReceiver.value = Pair(it.status, it.message)
                        }
                    }

                }

            }
        }
    }
}