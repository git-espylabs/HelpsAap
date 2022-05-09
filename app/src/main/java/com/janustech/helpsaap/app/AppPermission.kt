package com.janustech.helpsaap.app

import android.Manifest
import com.janustech.helpsaap.R

sealed class AppPermission(
    val permissionName: String, val requestCode: Int, val deniedMessageId: Int, val explanationMessageId: Int
) {
    companion object {
        val permissions: List<AppPermission> by lazy {
            listOf(
                ACCESS_FINE_LOCATION
            )
        }

        val PERMISSION_LOCATION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

        val PERMISSION_STORAGE = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    object ACCESS_FINE_LOCATION : AppPermission(
        Manifest.permission.ACCESS_FINE_LOCATION, 42,
        R.string.permission_required_text, R.string.permission_required_text
    )

    object ACCESS_STORAGE : AppPermission(
        Manifest.permission.READ_EXTERNAL_STORAGE, 47,
        R.string.permission_storage_required_text, R.string.permission_storage_required_text
    )
}