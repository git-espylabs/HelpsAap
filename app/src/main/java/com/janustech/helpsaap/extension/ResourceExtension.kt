package com.janustech.helpsaap.extension

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.ContextCompat

fun Resources.getHtmlSpannedString(@StringRes id: Int): Spanned = getString(id).toHtmlSpan()

fun Resources.getHtmlSpannedString(@StringRes id: Int, vararg formatArgs: Any): Spanned =
    getString(id, *formatArgs).toHtmlSpan()

fun Resources.getQuantityHtmlSpannedString(@PluralsRes id: Int, quantity: Int): Spanned =
    getQuantityString(id, quantity).toHtmlSpan()

fun Resources.getQuantityHtmlSpannedString(
    @PluralsRes id: Int,
    quantity: Int,
    vararg formatArgs: Any
): Spanned = getQuantityString(id, quantity, *formatArgs).toHtmlSpan()

fun String.toHtmlSpan(): Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
} else {
    Html.fromHtml(this)
}

@ColorInt
fun Context.resolveColorAttr(@AttrRes colorAttr: Int): Int {
    val resolvedAttr = resolveThemeAttr(colorAttr)
    // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
    val colorRes = if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else resolvedAttr.data
    return ContextCompat.getColor(this, colorRes)
}

fun Context.resolveColorStateList(@AttrRes colorAttr: Int): ColorStateList? {
    val resolvedAttr = resolveThemeAttr(colorAttr)
    // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
    val colorRes = if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else resolvedAttr.data
    return ContextCompat.getColorStateList(this, colorRes)
}


fun Context.resolveThemeAttr(@AttrRes attrRes: Int): TypedValue {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    return typedValue
}

fun Int.dpToPixel(): Float =
    (this * Resources.getSystem().displayMetrics.density)

fun Float.dpToPixel(): Float =
    (this * Resources.getSystem().displayMetrics.density)

fun Int.pixelToDp(): Float =
    this / Resources.getSystem().displayMetrics.density


@ColorInt
fun Context.getCompatColor(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}