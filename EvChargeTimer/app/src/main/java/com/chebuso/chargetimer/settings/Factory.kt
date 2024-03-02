package com.chebuso.chargetimer.settings

import android.app.Activity
import android.content.Context
import com.chebuso.chargetimer.notifications.NotificationScheduler
import com.chebuso.chargetimer.notifications.ResourceProvider


object Factory {
    fun createSettingsReader(context: Context): ISettingsReader =
        SharedPreferenceSettingsReader(context)

    fun createSettingsWriter(context: Context): ISettingsWriter =
        SharedPreferenceSettingsWriter(context)

    fun createScheduler(activity: Activity): NotificationScheduler {
        val settingsProvider = createSettingsReader(activity)
        val settingsWriter = createSettingsWriter(activity)
        val resourceProvider = ResourceProvider(activity)
        return NotificationScheduler(activity, settingsProvider, settingsWriter, resourceProvider)
    }
}