package com.janustech.helpsaap.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ActivityAppHomeBinding
import com.janustech.helpsaap.extension.setImageTint
import com.janustech.helpsaap.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppHomeActivity : BaseActivity<ActivityAppHomeBinding>(), View.OnClickListener, NavController.OnDestinationChangedListener {


    private val appHomeViewModel: AppHomeViewModel by viewModels()

    private var doubleBackToExitPressedOnce = false
    private lateinit var controller: NavController
    private lateinit var navHostFragment: NavHostFragment
    private val EXIT_DELAY = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLayoutBinding(R.layout.activity_app_home)
        setToolbarProperties(false, null)

        binding?.apply {
            viewModel = appHomeViewModel
            lifecycleOwner = this@AppHomeActivity
            viewParent = this@AppHomeActivity
        }
        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        controller = navHostFragment.navController
    }

    override fun onResume() {
        super.onResume()
        controller.addOnDestinationChangedListener(this)
    }

    override fun onPause() {
        super.onPause()
        controller.removeOnDestinationChangedListener(this)
    }

    override fun onBackPressed() {

        when {
            controller.currentDestination?.id == R.id.appHomeFragment -> {

                if (doubleBackToExitPressedOnce) {
                    val setIntent = Intent(Intent.ACTION_MAIN)
                    setIntent.addCategory(Intent.CATEGORY_HOME)
                    setIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(setIntent)
                    finish()
                }

                this.doubleBackToExitPressedOnce = true
                showToast(getString(R.string.exit_tap_alert))

                Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, EXIT_DELAY)
            }
            supportFragmentManager.backStackEntryCount > 0 -> {
                super.onBackPressed()
            }
            else -> {
                findNavController(R.id.fragmentContainerView).apply {
                    navigate(R.id.appHomeFragment, null, getNavOptions())
                }
            }
        }


    }

    override fun onCreateToolbar(): Toolbar? {
        return null
    }

    override fun onCreateLoader(): View? {
        return binding?.loadingView?.loaderView
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.l1 ->{
                findNavController(R.id.fragmentContainerView).apply {
                    navigate(R.id.appHomeFragment, null, getNavOptions())
                }
            }
            R.id.l2 ->{
                findNavController(R.id.fragmentContainerView).apply {
                    navigate(R.id.dealOfDayFragment, null, getNavOptions())
                }
            }
            R.id.l5 ->{
                findNavController(R.id.fragmentContainerView).apply {
                    navigate(R.id.editProfileFragment, null, getNavOptions())
                }
            }
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when(destination.id){
            R.id.appHomeFragment ->{
                binding?.apply {
                    btnHome.setImageTint(R.color.app_accent_color)
                    btnDeal.setImageTint(R.color.grey_disabled)
                    btnAds.setImageTint(R.color.grey_disabled)
                    btnNotification.setImageTint(R.color.grey_disabled)
                    btnProfile.setImageTint(R.color.grey_disabled)
                }
            }
            R.id.dealOfDayFragment ->{
                binding?.apply {
                    btnHome.setImageTint(R.color.grey_disabled)
                    btnDeal.setImageTint(R.color.app_accent_color)
                    btnAds.setImageTint(R.color.grey_disabled)
                    btnNotification.setImageTint(R.color.grey_disabled)
                    btnProfile.setImageTint(R.color.grey_disabled)
                }
            }
            R.id.editProfileFragment ->{
                binding?.apply {
                    btnHome.setImageTint(R.color.grey_disabled)
                    btnDeal.setImageTint(R.color.grey_disabled)
                    btnAds.setImageTint(R.color.grey_disabled)
                    btnNotification.setImageTint(R.color.grey_disabled)
                    btnProfile.setImageTint(R.color.app_accent_color)
                }
            }
        }
    }

    private fun getNavOptions(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.slide_from_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_from_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()
    }
}