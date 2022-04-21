package com.janustech.helpsaap.ui.startup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.janustech.helpsaap.R
import com.janustech.helpsaap.extension.launchActivity
import com.janustech.helpsaap.preference.AppPreferences
import com.janustech.helpsaap.ui.profile.LoginActivity

class Splash : AppCompatActivity() {

    private val DELAY = 1500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            if (AppPreferences.userId.isEmpty()) {
                this.launchActivity<LoginActivity>()
                finish()
            } else {
                this.launchActivity<AppIntroActivity>()
                finish()
            }
        }, DELAY)

    }
}