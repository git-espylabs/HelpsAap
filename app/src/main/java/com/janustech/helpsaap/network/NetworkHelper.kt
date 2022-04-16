package com.janustech.helpsaap.network

import com.janustech.helpsaap.network.response.ErrorResponse
import com.squareup.moshi.Moshi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
    return try {
        val result = Resource.success(apiCall.invoke())
        result
    } catch (throwable: Throwable) {
        getErrorData(throwable)
    }
}

suspend fun <T> safeApiCallback(apiCall: () -> Call<T>): Resource<T> {
    return suspendCoroutine {
        apiCall.invoke().enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val data = response.body()
                if (data != null) {
                    it.resume(Resource.success(data))
                } else {
                    it.resume(Resource.httpError(response.code(), response.message(), null))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                it.resume(getErrorData(t))
            }
        })
    }
}

private fun getErrorData(throwable: Throwable) = when (throwable) {
    is IOException -> Resource.networkError(throwable.message ?: "Network error", null)
    is SocketTimeoutException -> Resource.networkError(
        throwable.message ?: "Network error",
        null
    )
    is HttpException -> {
        val errorBody = convertErrorBody(throwable)
        Resource.httpError(
            errorBody?.status ?: throwable.code(),
            errorBody?.title ?: "",
            null
        )
    }
    else -> Resource.genericError(throwable.message ?: "Unknown error occurred", null)
}

private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
    return try {
        throwable.response()?.errorBody()?.source()?.let {
            val moshiAdapter = Moshi.Builder().build().adapter(ErrorResponse::class.java)
            moshiAdapter.fromJson(it)
        }
    } catch (exception: Exception) {
        null
    }
}