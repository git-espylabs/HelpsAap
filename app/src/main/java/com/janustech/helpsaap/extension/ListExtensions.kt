package com.janustech.helpsaap.extension

inline fun <T> Iterable<T>.sumBy(selector: (T) -> Float): Float {
    var sum: Float = 0F
    for (element in this) {
        sum += selector(element)
    }
    return sum
}