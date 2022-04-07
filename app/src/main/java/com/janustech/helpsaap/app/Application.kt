package com.janustech.helpsaap.app

import android.app.Application
import android.content.Context
import com.janustech.helpsaap.app.AppSettings.Companion.APP_PREF
import com.janustech.helpsaap.preference.PreferenceProvider

private lateinit var appContext: Context

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
//        DatabaseProvider().initDb(appContext)
        PreferenceProvider.init(appContext, APP_PREF)
    }
}