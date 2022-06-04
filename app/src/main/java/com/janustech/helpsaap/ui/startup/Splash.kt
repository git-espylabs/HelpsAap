package com.janustech.helpsaap.ui.startup

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.janustech.helpsaap.R
import com.janustech.helpsaap.extension.launchActivity
import com.janustech.helpsaap.preference.AppPreferences
import com.janustech.helpsaap.ui.home.AppHomeActivity

class Splash : AppCompatActivity() {

    private val DELAY = 1500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            this.launchActivity<AppIntroActivity>()
            finish()
        }, DELAY)

    }
}