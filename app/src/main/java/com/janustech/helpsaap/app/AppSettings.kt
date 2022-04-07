package com.janustech.helpsaap.app

internal class AppSettings {

    companion object{
        const val APP_PREF = "com.janustech.helpsaap"

        const val CACHED_DB_PATH = "database/capapp_config_db.db"
        const val DB_NAME = "capapp.db"

        const val NETWORK_READ_TIME_OUT = 30 * 1000
        const val NETWORK_CONNECTION_TIME_OUT = 10 * 1000

        const val CAP_FILE_PROVIDER = "fileprovider"

        /*val endPoints = HttpEndPoints

        val cacheControl = NetworkRequestHeader("Cache-Control", "no-cache")
        val contentType = NetworkRequestHeader("Content-Type", "application/json")*/
    }
}