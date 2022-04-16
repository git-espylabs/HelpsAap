package com.janustech.helpsaap.network

enum class Status(val type: Int) {
    SUCCESS(0),
    GENERIC_ERROR(1),
    NETWORK_ERROR(2),
    HTTP_ERROR(3),
    LOADING(4),
    DATA_ERROR(5)
}