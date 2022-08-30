package com.janustech.helpsaap.ui.home

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ActivityAppHomeBinding
import com.janustech.helpsaap.extension.launchActivity
import com.janustech.helpsaap.extension.setImageTint
import com.janustech.helpsaap.model.LocationDataModel
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.preference.AppPreferences
import com.janustech.helpsaap.ui.base.BaseActivity
import com.janustech.helpsaap.ui.startup.AppIntroActivity
import com.janustech.helpsaap.utils.EditLocationListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AppHomeActivity : BaseActivity<ActivityAppHomeBinding>(), View.OnClickListener, NavController.OnDestinationChangedListener, EditLocationListener {


    private val appHomeViewModel: AppHomeViewModel by viewModels()

    private var doubleBackToExitPressedOnce = false
    private lateinit var controller: NavController
    private lateinit var navHostFragment: NavHostFragment
    private val EXIT_DELAY = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLayoutBinding(R.layout.activity_app_home)
        setToolbarProperties(false, null)
        setObserver()

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
                launchActivity<AppIntroActivity>()
                this.finish()
            }
            controller.currentDestination?.id == R.id.dealOfDayFragment -> {
                appHomeViewModel.selectedFromDealDateTv = ""
                appHomeViewModel.selectedToDealDateTv = ""
                super.onBackPressed()
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
            R.id.l3 ->{
                findNavController(R.id.fragmentContainerView).apply {
                    navigate(R.id.advertiseFragment, null, getNavOptions())
                }
            }
            R.id.l4 ->{
                findNavController(R.id.fragmentContainerView).apply {
                    navigate(R.id.notificationsFragment, null, getNavOptions())
                }
            }
            R.id.l5 ->{
                findNavController(R.id.fragmentContainerView).apply {
                    navigate(R.id.editProfileFragment, null, getNavOptions())
                }
            }
            R.id.tvLocation ->{
                EditLocationBottomSheetDialogFragment(appHomeViewModel, this).show(
                    supportFragmentManager,
                    "EditLocationFragment"
                )
            }

            R.id.btnProfileIco ->{
                binding?.btnProfileIco?.apply {
                    showPopup(this)
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
                appHomeViewModel.selectedFromDealDateTv = ""
                appHomeViewModel.selectedToDealDateTv = ""
                binding?.apply {
                    btnHome.setImageTint(R.color.grey_disabled)
                    btnDeal.setImageTint(R.color.app_accent_color)
                    btnAds.setImageTint(R.color.grey_disabled)
                    btnNotification.setImageTint(R.color.grey_disabled)
                    btnProfile.setImageTint(R.color.grey_disabled)
                }
            }
            R.id.advertiseFragment ->{
                binding?.apply {
                    btnHome.setImageTint(R.color.grey_disabled)
                    btnDeal.setImageTint(R.color.grey_disabled)
                    btnAds.setImageTint(R.color.app_accent_color)
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
            R.id.notificationsFragment ->{
                binding?.apply {
                    btnHome.setImageTint(R.color.grey_disabled)
                    btnDeal.setImageTint(R.color.grey_disabled)
                    btnAds.setImageTint(R.color.grey_disabled)
                    btnNotification.setImageTint(R.color.app_accent_color)
                    btnProfile.setImageTint(R.color.grey_disabled)
                }
            }
        }
    }

    override fun onLocationSelected(location: LocationDataModel) {
        location.let {
            appHomeViewModel.userLocationName = it.toString()
            appHomeViewModel.userLocationId = it.id
            binding?.apply {
                tvLocation.text = it.toString()
            }
        }
    }

    private fun setObserver(){
        appHomeViewModel.aboutUsRespReceiver.observe(this){ res->
            try {
                res?.let {
                    when(it.status){
                        Status.SUCCESS ->{
                            hideProgress()
                            it.data?.data?.let { it1 -> showAboutPopup(it1?.about) }
                        }
                        Status.LOADING -> {
                            showProgress()
                        }
                        else ->{
                            hideProgress()
                            showToast(it.message?:"Invalid Server Response")
                        }
                    }
                }
            } catch (e: Exception) {
            }

        }
    }

    private fun showPopup(v : View){
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_home, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.actionLogout-> {
                    AppPreferences.clearAll()
                    launchActivity<AppIntroActivity>{
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    }
                    this.finish()
                }
                R.id.actionAbout-> {
                    //appHomeViewModel.getAboutUs()
                    showAboutUsDialog()
                }
            }
            true
        }
        popup.show()
    }

    private fun showAboutUsDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.fragment_terms_conditions)
        val title = dialog.findViewById(R.id.title) as TextView
        title.text = "About Us"
        val tv_close = dialog.findViewById(R.id.tv_close) as TextView
        val webView = dialog.findViewById(R.id.webView) as WebView
        val layoutParams = dialog.window!!.attributes
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.attributes = layoutParams
        var tc_url = "https://helpsaap.com/aboutus"
        webView.apply {
            loadUrl(tc_url)
            settings.also {
                it.loadsImagesAutomatically = true
                it.javaScriptEnabled = true;
            }
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    /*if (activity is AppHomeActivity) {
                        (activity as AppHomeActivity).hideProgress()
                    } else if (activity is SignupActivity) {
                        (activity as SignupActivity).hideProgress()
                    }*/
                }
            }
        }
        tv_close.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }

    private fun showAboutPopup(text: String){
        val bun = Bundle()
        bun.putString("aboutus",text)
        findNavController(R.id.fragmentContainerView).apply {
            navigate(R.id.aboutUsFragment, bun, getNavOptions())
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