package com.chebuso.chargetimer.permissions

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import androidx.core.app.ActivityCompat
import com.chebuso.chargetimer.R

class PermissionRequestDialog internal constructor(
    private val dialogTitle: String,
    private val dialogMessage: String,
    private val permissions: Array<String>,
    private val activity: Activity,
    private val permissionResultLauncher: PermissionActivityResultLauncher,
){
    fun requestPermissionsIfNeeded() {
        if (shouldShowRequestPermission()) {
            showRationaleDialog()
        } else {
            requestPermissions()
        }
    }

    private fun shouldShowRequestPermission(): Boolean =
        permissions.any { ActivityCompat.shouldShowRequestPermissionRationale(activity, it) }

    private fun showRationaleDialog() {
        Log.i(TAG, "Show permissions rationale dialog")
        AlertDialog.Builder(activity).create().apply {
            setTitle(dialogTitle)
            setMessage(dialogMessage)

            setButton(
                AlertDialog.BUTTON_NEGATIVE,
                activity.getString(R.string.permission_dialog_forbid)
            ) { dialog, _ ->
                dialog.dismiss()
            }

            setButton(
                AlertDialog.BUTTON_POSITIVE,
                activity.getString(R.string.permission_dialog_allow)
            ) { dialog, _ ->
                dialog.dismiss()
                requestPermissions()
            }
        }.show()
    }

    private fun requestPermissions() {
        Log.i(TAG, "Request permissions")
        permissionResultLauncher.activityResultLauncher.launch(permissions)
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}