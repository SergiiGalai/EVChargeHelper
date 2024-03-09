package com.chebuso.chargetimer

import android.app.Activity
import android.content.Context
import com.chebuso.chargetimer.notifications.IResourceProvider
import com.chebuso.chargetimer.notifications.application.NotificationFactory
import com.chebuso.chargetimer.notifications.NotificationScheduler
import com.chebuso.chargetimer.notifications.NotificatorFactory
import com.chebuso.chargetimer.notifications.ResourceProvider
import com.chebuso.chargetimer.settings.ISettingsReader
import com.chebuso.chargetimer.settings.ISettingsWriter
import com.chebuso.chargetimer.settings.SharedPreferenceSettingsReader
import com.chebuso.chargetimer.settings.SharedPreferenceSettingsWriter


object Factory {
    fun settingsReader(context: Context): ISettingsReader =
        SharedPreferenceSettingsReader(context)

    fun settingsWriter(context: Context): ISettingsWriter =
        SharedPreferenceSettingsWriter(context)

    fun notificationScheduler(activity: Activity): NotificationScheduler {
        val notificatorFactory = notificatorFactory(activity)
        return NotificationScheduler(notificatorFactory)
    }

    fun notificationFactory(context: Context): NotificationFactory {
        val resourceProvider = resourceProvider(context)
        return NotificationFactory(resourceProvider, context)
    }

    private fun notificatorFactory(activity: Activity): NotificatorFactory {
        return NotificatorFactory(
            activity,
            settingsReader(activity),
            resourceProvider(activity),
            settingsWriter(activity)
        )
    }

    private fun resourceProvider(context: Context): IResourceProvider = ResourceProvider(context)
}