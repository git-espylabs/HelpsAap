package com.janustech.helpsaap.extension

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import java.lang.reflect.Field


internal fun View.setStateAttributionBackground(cornerRadius: Int = 16, color: Int) {
    setBackgroundDrawableWithColorScheme(ContextCompat.getColor(context, color), cornerRadius)
}

internal fun View.setBackgroundDrawableWithColorScheme(
    @ColorInt color: Int,
    cornerRadius: Int = 16
) {
    this.background = GradientDrawable().apply {
        this.cornerRadius = cornerRadius.toFloat()
        setColor(color)
    }
}

internal fun View.setStateAttributionBorder(strokeWidth: Int = 4, cornerRadius: Int = 12, color: Int) {
    setBorderColor(cornerRadius, strokeWidth, ContextCompat.getColor(context, color))
}

internal fun View.setBorderColor(cornerRadius: Int, strokeWidth: Int, @ColorInt primaryColor: Int) {
    val radius = cornerRadius.toFloat()
    this.background = GradientDrawable().apply {
        setCornerRadius(radius)
        setStroke(strokeWidth, primaryColor)
    }
}

@SuppressLint("UseCompatLoadingForDrawables")
internal fun EditText.setStateAttributionCursorAndTextColor(color: Int) {
    val parseColor = ContextCompat.getColor(context, color)
    setCursorAndTextColor(parseColor)
}

internal fun EditText.setCursorAndTextColor(@ColorInt color: Int) {
    try {
        setTextColor(color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            textCursorDrawable?.setTint(color)
            textSelectHandle?.setTint(color)
            textSelectHandleLeft?.setTint(color)
            textSelectHandleRight?.setTint(color)
        } else {
            val fCursorDrawableRes: Field =
                TextView::class.java.getDeclaredField("mCursorDrawableRes")
            fCursorDrawableRes.isAccessible = true
            val mCursorDrawableRes = fCursorDrawableRes.getInt(this)
            val fEditor: Field = TextView::class.java.getDeclaredField("mEditor")
            fEditor.isAccessible = true
            fEditor.get(this)?.let { editor ->
                val clazz: Class<*> = editor.javaClass
                val fCursorDrawable: Field = clazz.getDeclaredField("mCursorDrawable")
                fCursorDrawable.isAccessible = true
                val drawables = arrayOfNulls<Drawable>(2)
                val drawable = ContextCompat.getDrawable(context, mCursorDrawableRes)?.apply {
                    setTint(color)
                }
                drawables[0] = drawable
                drawables[1] = drawable
                fCursorDrawable[editor] = drawables
            }
        }
    } catch (ex: Throwable) {
        Log.e("Cursor", "${ex.message}")
    }
}

internal fun TextInputLayout.setStateAttributionCursorAndTextColor(color: Int) {
    setBoxStrokeLineColor(ContextCompat.getColor(context, color))
}

internal fun TextInputLayout.setBoxStrokeLineColor(@ColorInt color: Int) {
    boxStrokeColor = color
}

internal fun ImageButton.setStateAttributionTint(color: Int) {
    setColorFilter(ContextCompat.getColor(context, color))
}

internal fun TextView.setDrawableTint(color: Int){
    compoundDrawables.forEach { drawable ->
        drawable?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN)
    }
}

internal fun ImageView.setImageTint(color: Int){
    imageTintList =
        (ColorStateList.valueOf(ContextCompat.getColor(context, color)))
}