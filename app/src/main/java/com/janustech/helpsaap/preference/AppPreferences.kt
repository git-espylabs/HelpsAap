package com.janustech.helpsaap.preference

object AppPreferences {

    private const val USER_ID = "user_id"


    var userId: String by PreferenceProvider(USER_ID, "")
}