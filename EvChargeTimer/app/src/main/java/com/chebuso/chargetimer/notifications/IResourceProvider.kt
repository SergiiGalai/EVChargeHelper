package com.chebuso.chargetimer.notifications

import android.content.Context
import android.net.Uri
import com.chebuso.chargetimer.R


interface IResourceProvider {
    val applicationNotificationSoundUri: Uri
}

class ResourceProvider(private val context: Context) : IResourceProvider {

    override val applicationNotificationSoundUri: Uri
        get() = Uri.parse("android.resource://${context.packageName}/${R.raw.carhorn4}")
}