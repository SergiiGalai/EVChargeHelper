package com.chebuso.chargetimer.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


fun Context.isPermissionGranted(permission: String) =
    ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

object PermissionHelper {
    fun isPermissionsGranted(grantResults: IntArray): Boolean {
        // At least one result must be checked.
        if (grantResults.isEmpty())
            return false

        // Verify that each required permission has been granted, otherwise return false.
        return grantResults.all { it == PackageManager.PERMISSION_GRANTED }
    }

    fun isFullCalendarPermissionsGranted(context: Context): Boolean {
        return context.isPermissionGranted(Manifest.permission.READ_CALENDAR)
                && context.isPermissionGranted(Manifest.permission.WRITE_CALENDAR)
    }
}

