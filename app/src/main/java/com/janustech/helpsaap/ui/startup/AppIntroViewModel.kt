package com.janustech.helpsaap.ui.startup

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janustech.helpsaap.network.Resource
import com.janustech.helpsaap.network.response.ApiResponse
import com.janustech.helpsaap.network.response.LanguageListResponseData
import com.janustech.helpsaap.usecase.AppIntroUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppIntroViewModel @Inject constructor(private val appIntroUseCase: AppIntroUseCase): ViewModel() {

    var userLocation = "Edappally, Ernakulam"
    var userLanguage = "Malayalam"

    private val _languageListReceiver = MutableLiveData<Resource<ApiResponse<List<LanguageListResponseData>>>>()
    val languageListReceiver: LiveData<Resource<ApiResponse<List<LanguageListResponseData>>>>
        get() = _languageListReceiver

    init {
        getLanguages()
    }

    private fun getLanguages(){
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
}