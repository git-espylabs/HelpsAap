package com.janustech.helpsaap.ui.profile

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ActivityLoginBinding
import com.janustech.helpsaap.ui.base.BaseActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>(
    R.layout.activity_login,
    false,
    null
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateToolbar(): Toolbar? {
        return null
    }
}