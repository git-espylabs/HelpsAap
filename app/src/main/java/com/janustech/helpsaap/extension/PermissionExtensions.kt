package com.janustech.helpsaap.extension

import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.janustech.helpsaap.app.AppPermission

fun isLollipopOrBellow(): Boolean = (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.LOLLIPOP)

fun Fragment.isPermissionGranted(permission: AppPermission) = run {
    context?.let {
        (PermissionChecker.checkSelfPermission(it, permission.permissionName
        ) == PermissionChecker.PERMISSION_GRANTED)
    } ?: false
}


fun Fragment.shouldShowRationale(permission: AppPermission) = run {
    shouldShowRequestPermissionRationale(permission.permissionName)
}

fun Fragment.requestPermission(resultLauncher: ActivityResultLauncher<Array<String>>, permissions: Array<String>){
    resultLauncher.launch(permissions)
}

inline fun Fragment.handlePermission(permission: AppPermission,
                                     onGranted: (AppPermission) -> Unit,
                                     onDenied: (AppPermission) -> Unit,
                                     onRationaleNeeded: (AppPermission) -> Unit) {
    when {
        isLollipopOrBellow() || isPermissionGranted(permission) -> onGranted(permission)
        shouldShowRationale(permission) -> onRationaleNeeded(permission)
        else -> onDenied(permission)
    }
}
