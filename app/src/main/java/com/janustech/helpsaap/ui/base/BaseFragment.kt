package com.janustech.helpsaap.ui.base

import android.graphics.Color
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    val baseActivity: BaseActivity<*>?
        get() = activity as BaseActivity<*>?

    fun setToolbarTitle(@StringRes id: Int) {
        activity?.let {
            (it as BaseActivity<*>).setToolbarTitle(id)
        }
    }

    fun showAlertDialog(@StringRes messageId: Int) {
        baseActivity?.showAlertDialog(messageId)
    }

    fun showAlertDialog(message: String) {
        baseActivity?.showAlertDialog(message)
    }

    fun showToast(@StringRes messageId: Int) {
        showToast(getString(messageId))
    }

    fun showToast(message: String) {
        baseActivity?.showToast(message)
    }

    fun getColor(@ColorRes colorRes: Int) = ContextCompat.getColor(requireContext(), colorRes)

    fun getDimension(@DimenRes dimenRes: Int) = resources.getDimension(dimenRes)
}