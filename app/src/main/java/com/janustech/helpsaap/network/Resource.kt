package com.janustech.helpsaap.network

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?,
    var errorCode: Int = 0
) {
    fun isSuccess(): Boolean = status == Status.SUCCESS

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> networkError(msg: String, data: T?): Resource<T> {
            return Resource(Status.NETWORK_ERROR, data, msg)
        }

        fun <T> httpError(errorCode: Int, msg: String, data: T?): Resource<T> {
            return Resource(Status.HTTP_ERROR, data, msg, errorCode)
        }

        fun <T> genericError(msg: String, data: T?): Resource<T> {
            return Resource(Status.GENERIC_ERROR, data, msg)
        }

        fun <T> dataError(msg: String): Resource<T> {
            return Resource(Status.DATA_ERROR, null, msg)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}
