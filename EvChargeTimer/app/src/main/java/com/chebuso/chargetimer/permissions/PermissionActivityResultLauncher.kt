package com.chebuso.chargetimer.permissions

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher

data class PermissionActivityResultLauncher(
    val activity: Activity,
    val activityResultLauncher: ActivityResultLauncher<Array<String>>,
)