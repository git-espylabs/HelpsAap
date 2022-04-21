package com.janustech.helpsaap.ui.startup

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ActivitySignupBinding
import com.janustech.helpsaap.ui.base.BaseActivity
import com.janustech.helpsaap.ui.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupActivity : BaseActivity<ActivitySignupBinding>() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLayoutBinding(R.layout.activity_signup)
        setToolbarProperties(false, null)
        binding?.lifecycleOwner = this
    }

    override fun onCreateToolbar(): Toolbar? {
        return null
    }

    override fun onCreateLoader(): View? {
        return binding?.loadingView?.loaderView
    }
}