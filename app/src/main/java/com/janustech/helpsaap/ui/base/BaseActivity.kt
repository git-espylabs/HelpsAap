package com.janustech.helpsaap.ui.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.NavigationRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.janustech.helpsaap.extension.negativeButton
import com.janustech.helpsaap.extension.positiveButton
import com.janustech.helpsaap.extension.showAppDialog
import com.janustech.helpsaap.R


abstract class BaseActivity<VB : ViewDataBinding>(
    @LayoutRes private val layoutResID: Int,
    private val showHomeAsUp: Boolean = false,
    @StringRes private val titleRes: Int? = null
) : AppCompatActivity() {

    private var _binding: VB? = null
    val binding: VB? get() = _binding

    private var alertDialog: Dialog? = null
    private var toolbar: Toolbar? = null

    abstract fun onCreateToolbar(): Toolbar?

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, layoutResID)

        initToolbar()
    }

    private fun initToolbar() {
        toolbar = onCreateToolbar()
        toolbar?.apply {
            setSupportActionBar(this)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(showHomeAsUp)
                setDisplayShowTitleEnabled(false)
            }
            titleRes?.let { setToolbarTitle(it) }
        }
    }

    open fun onCreateLoader(): View? =
        throw UnsupportedOperationException(
            "Inorder to show progress," +
                    " you need to override onCreateLoader...!"
        )

    fun setToolbarTitle(@StringRes id: Int) {
        try {
            toolbar?.apply {
                findViewById<TextView>(R.id.toolbarTitle)?.text = getString(id)
            }
        } catch (e: Exception) {
        }
    }

    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }


    fun setNavGraph(
        @IdRes fragmentContainerId: Int,
        @NavigationRes graphResId: Int,
        @IdRes startDestinationId: Int?
    ): NavController {
        val navHostFragment = supportFragmentManager
            .findFragmentById(fragmentContainerId) as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater

        val navGraph = graphInflater.inflate(graphResId)
        startDestinationId?.let { navGraph.setStartDestination(it) }
        val navController = navHostFragment.navController
        navController.setGraph(navGraph, intent.extras)
        return navController
    }

    fun showAlertDialog(@StringRes messageId: Int) {
        showAlertDialog(getString(messageId))
    }

    fun showAlertDialog(message: String) {
        dismissAlertDialogIfRequired()
        alertDialog = showAppDialog(cancelable = true, cancelableTouchOutside = true) {
            this.setMessage(message)
            this.positiveButton()
        }
    }

    private fun dismissAlertDialogIfRequired() {
        if (alertDialog?.isShowing == true) {
            alertDialog?.dismiss()
        }
    }

    fun showProgress() {

        val view = onCreateLoader()
        view?.apply {
            view.visibility = View.VISIBLE
        }
    }

    fun hideProgress() {
        val view = onCreateLoader()
        view?.apply {
            view.visibility = View.GONE
        }
    }

    fun hideKeyboard() {
        this.currentFocus?.let { v ->
            v.clearFocus()
            try {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                    ?.hideSoftInputFromWindow(v.windowToken, 0)
            } catch (_: Exception) {
            }
        }
    }

    fun showConfirmAlertDialog(
        @StringRes messageId: Int,
        @StringRes positiveButtonId: Int,
        @StringRes negativeButtonId: Int,
        positiveAction: () -> Unit
    ) {
        dismissAlertDialogIfRequired()
        alertDialog = showAppDialog(
            cancelable = true,
            cancelableTouchOutside = true
        ) {
            this.setMessage(getString(messageId))
            this.positiveButton(getString(positiveButtonId)) { positiveAction.invoke() }
            this.negativeButton(getString(negativeButtonId))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissAlertDialogIfRequired()
        _binding = null
    }
}