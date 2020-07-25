package com.yukinonlab.yukinoncolorpicker.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat



class PermissionHelper{

}

fun checkPermissions (context: Context, REQUIRED_PERMISSIONS: Array<String>): Boolean {
    for (permission in REQUIRED_PERMISSIONS) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }
    return true
}