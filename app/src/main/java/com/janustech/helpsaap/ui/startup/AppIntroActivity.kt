package com.janustech.helpsaap.ui.startup

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ActivityAppIntroBinding
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AppIntroActivity : BaseActivity<ActivityAppIntroBinding>() {

    private val viewModel: AppIntroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLayoutBinding(R.layout.activity_app_intro)
        setToolbarProperties(false, null)
        binding?.lifecycleOwner = this

        setObserver()

        val startDestination = if (viewModel.userLocationId == "" || viewModel.userLanguageId == "") R.id.selectLocationFragment else R.id.appIntroHome
        setNavGraph(
            R.id.fragmentContainerView,
            R.navigation.app_intro_nav_graoh,
            startDestination
        )

    }

    override fun onResume() {
        super.onResume()
        viewModel.getAppVersion()
    }

    override fun onCreateToolbar(): Toolbar? {
        return null
    }

    override fun onCreateLoader(): View? {
        return binding?.loadingView?.loaderView
    }



    private fun setObserver(){
        viewModel.appversionResponseReceiver?.observe(this){ res->
            try {
                res?.let {
                    when(it.status){
                        Status.SUCCESS ->{
                            val  dat = it.data?.data?.get(0)
                            val curVer = getVersion()
                            curVer?.let { ver->
                                val currversioncode = ver.first
                                val currversionname = ver.second
                                dat?.let { verobj ->
                                    if (verobj.version_code.toInt() > currversioncode && verobj.version_name != currversionname){
                                        showVersionAlertDialog(verobj.version_name)
                                    }
                                }
                            }
                        }
                        Status.LOADING -> {

                        }
                        else ->{

                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun showVersionAlertDialog(vername: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Update available! - v$vername\nYou are using an outdated version of the app. Update the app to continue using HelpsAap services with latest features.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setCancelable(false)
        builder.setPositiveButton("Update"){dialogInterface, which ->
            val  intent = Intent(Intent.ACTION_VIEW);

            //Copy App URL from Google Play Store.
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.janustech.helpsaap"));

            startActivity(intent);
        }

        builder.setNegativeButton("Exit"){dialogInterface, which ->
            dialogInterface.dismiss()
            this.finish()
        }

        val versionAlertDialog = builder.create()
        versionAlertDialog?.setCancelable(false)
        versionAlertDialog?.show()
    }

    fun getVersion(): Pair<Long, String>?{
        var ver:Pair<Long, String>? = null
        try {
            val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            val version = pInfo.versionName
            val versionCode = pInfo.longVersionCode
            ver = Pair(versionCode, version)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ver
    }
}