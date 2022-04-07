@file:Suppress("unused", "UNUSED_ANONYMOUS_PARAMETER")

package com.janustech.helpsaap.extension

import android.app.Dialog
import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog

/**
 * Called when an alert dialog positive action is required.
 *
 * @param text The positive button text.
 * @param handleClick  action click receiver.
 */
fun AlertDialog.Builder.positiveButton(
    text: String,
    handleClick: (i: Int) -> Unit = {},
) {
    this.setPositiveButton(text) { dialogInterface, i -> handleClick(i) }
}

/**
 * Called when an alert dialog positive action is required.
 *
 * @param textResource The positive button text resource id.
 * @param handleClick  action click receiver.
 */
fun AlertDialog.Builder.positiveButton(
    @StringRes textResource: Int,
    handleClick: (i: Int) -> Unit = {},
) {
    this.setPositiveButton(textResource) { dialogInterface, i -> handleClick(i) }
}

/**
 * Called when an alert dialog positive action is required.
 *
 * @param handleClick  action click receiver.
 */
fun AlertDialog.Builder.positiveButton(
    handleClick: (i: Int) -> Unit = {},
) {
    this.setPositiveButton(android.R.string.ok) { dialogInterface, i -> handleClick(i) }
}

/**
 * Called when an alert dialog negative action is required.
 *
 * @param text The positive button text.
 * @param handleClick  action click receiver.
 */
fun AlertDialog.Builder.negativeButton(
    text: String,
    handleClick: (i: Int) -> Unit = {},
) {
    this.setNegativeButton(text) { dialogInterface, i -> handleClick(i) }
}

/**
 * Called when an alert dialog negative action is required.
 *
 * @param textResource The negative button text resource id.
 * @param handleClick  action click receiver.
 */
fun AlertDialog.Builder.negativeButton(
    @StringRes textResource: Int,
    handleClick: (i: Int) -> Unit = {},
) {
    this.setNegativeButton(textResource) { dialogInterface, i -> handleClick(i) }
}

/**
 * Called when an alert dialog negative action is required.
 *
 * @param handleClick  action click receiver.
 */
fun AlertDialog.Builder.negativeButton(
    handleClick: (i: Int) -> Unit = {},
) {
    this.setNegativeButton(android.R.string.cancel) { dialogInterface, i -> handleClick(i) }
}

/**
 * Called when an alert dialog positive action is required.
 *
 * @param text The positive button text.
 * @param handleClick  action click receiver.
 */
fun AlertDialog.Builder.neutralButton(text: String, handleClick: (i: Int) -> Unit = {}) {
    this.setNeutralButton(text) { dialogInterface, i -> handleClick(i) }
}

fun Context.showAppDialog(
    cancelable: Boolean = false,
    cancelableTouchOutside: Boolean = false,
    builderFunction: AlertDialog.Builder.() -> Any,
): Dialog {
    val builder = AlertDialog.Builder(this)
    builder.builderFunction()
    val dialog = builder.create()

    dialog.setCancelable(cancelable)
    dialog.setCanceledOnTouchOutside(cancelableTouchOutside)
    dialog.show()
    return dialog
}

interface DialogClickListener {

    fun positiveButtonClick()
    fun negativeButtonClick()
}

interface DialogAllClickListener : DialogClickListener {
    fun neutralButtonClick()
}