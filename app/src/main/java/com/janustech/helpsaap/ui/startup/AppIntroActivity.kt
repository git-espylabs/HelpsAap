package com.janustech.helpsaap.ui.startup

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ActivityAppIntroBinding
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

        val startDestination = if (viewModel.userLocationId == "" || viewModel.userLanguageId == "") R.id.selectLocationFragment else R.id.appIntroHome
        setNavGraph(
            R.id.fragmentContainerView,
            R.navigation.app_intro_nav_graoh,
            startDestination
        )
    }

    override fun onCreateToolbar(): Toolbar? {
        return null
    }

    override fun onCreateLoader(): View? {
        return binding?.loadingView?.loaderView
    }
}