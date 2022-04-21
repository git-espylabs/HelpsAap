package com.janustech.helpsaap.preference

object AppPreferences {

    private const val USER_ID = "user_id"
    private const val USER_DATA = "user_data"
    private const val USER_LOCATION_ID = "user_location_id"
    private const val USER_LOCATION = "user_location"
    private const val USER_LANGUAGE_ID = "user_language_id"
    private const val USER_LANGUAGE = "user_language"


    var userId: String by PreferenceProvider(USER_ID, "")
    var userData: String by PreferenceProvider(USER_DATA, "")
    var userLocationId: String by PreferenceProvider(USER_LOCATION_ID, "")
    var userLocation: String by PreferenceProvider(USER_LOCATION, "")
    var userLanguageId: String by PreferenceProvider(USER_LANGUAGE_ID, "")
    var userLanguage: String by PreferenceProvider(USER_LANGUAGE, "")
}