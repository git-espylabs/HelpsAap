package com.janustech.helpsaap.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel(), CoroutineScope {
    var isLoading = false

    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("handling error: ${throwable.message}")
    }

    fun runIfNotInProgress(method: () -> Unit) {
        if (!isLoading) {
            viewModelScope.launch {
                isLoading = true
                method.invoke()
            }
        }
    }
}