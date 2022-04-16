package com.janustech.helpsaap.ui.profile

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ActivityLoginBinding
import com.janustech.helpsaap.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLayoutBinding(R.layout.activity_login)
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