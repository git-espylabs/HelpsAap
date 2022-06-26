package com.janustech.helpsaap.ui.base

import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment

open class BaseDialogFragment : DialogFragment() {
    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            window.attributes = window.attributes.apply {
                gravity = Gravity.CENTER
                flags = flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND
            }
            window.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
}