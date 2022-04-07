package com.janustech.helpsaap.extension

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


inline fun <reified T> String.deserialize(): T {
    return Json {
        ignoreUnknownKeys = true
    }.decodeFromString(this)
}

inline fun <reified T> T.serialize(): String {
    return Json.encodeToString(this)
}