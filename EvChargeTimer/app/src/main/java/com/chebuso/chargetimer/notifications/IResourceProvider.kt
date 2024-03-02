package com.chebuso.chargetimer.notifications

import android.app.Activity
import android.net.Uri
import com.chebuso.chargetimer.R


interface IResourceProvider {
    val applicationNotificationSoundUri: Uri
}

class ResourceProvider(private val activity: Activity) : IResourceProvider {

    override val applicationNotificationSoundUri: Uri
        get() = Uri.parse("android.resource://${activity.packageName}/${R.raw.carhorn4}")
}