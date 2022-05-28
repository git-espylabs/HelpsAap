package com.janustech.helpsaap.extension

import java.util.*
import java.util.regex.Pattern

fun Double.formatMiles(): String {
    if (0 >= this) return ">999 MI"
    return if (0 < this && this > 999) ">999 MI" else "$this MI"
}

fun Boolean.toInt() = if (this) 1 else 0

fun Date.toLocalTimeFromUtc(): Date {
    return Date(this.time + Calendar.getInstance().timeZone.getOffset(this.time))
}

fun Date.toUTc(): Date {
    return Date(this.time - Calendar.getInstance().timeZone.getOffset(this.time))
}

fun Int.toScoreItemValue() = if (this < 0) null else this

fun <E> java.util.ArrayList<E>.nullIfEmpty(): java.util.ArrayList<E>? =
    if (this.isEmpty()) null else this

internal fun String.isValidPhoneNumber(): Boolean{
    if(!Pattern.matches("[a-zA-Z]+", this)) {
        return this.length in 7..13;
    }
    return false;
}

internal fun String.isNumeric(): Boolean {
    val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
    return matches(regex)
}