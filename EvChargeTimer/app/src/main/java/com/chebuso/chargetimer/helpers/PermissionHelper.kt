package com.chebuso.chargetimer.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


fun Context.isPermissionGranted(permission: String) =
    ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

object PermissionHelper {
    fun isFullCalendarPermissionsGranted(context: Context): Boolean {
        return context.isPermissionGranted(Manifest.permission.READ_CALENDAR)
                && context.isPermissionGranted(Manifest.permission.WRITE_CALENDAR)
    }
}

