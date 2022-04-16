package com.janustech.helpsaap.preference

object AppPreferences {

    private const val USER_ID = "user_id"
    private const val USER_DATA = "user_data"


    var userId: String by PreferenceProvider(USER_ID, "")
    var userData: String by PreferenceProvider(USER_DATA, "")
}