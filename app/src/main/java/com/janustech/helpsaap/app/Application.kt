package com.janustech.helpsaap.app

import android.app.Application
import android.content.Context
import com.janustech.helpsaap.app.AppSettings.Companion.APP_PREF
import com.janustech.helpsaap.preference.PreferenceProvider
import com.janustech.helpsaap.utils.CommonUtils
import com.razorpay.Checkout
import dagger.hilt.android.HiltAndroidApp

private lateinit var appContext: Context

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        CommonUtils.writeLogFile(this, "******** STARTUP ********")
        appContext = this
        PreferenceProvider.init(appContext, APP_PREF)
        Checkout.preload(this)
    }
}