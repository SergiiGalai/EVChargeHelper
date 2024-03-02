package com.chebuso.chargetimer.notifications

import android.app.Activity
import com.chebuso.chargetimer.helpers.PermissionHelper.isPermissionsGranted
import com.chebuso.chargetimer.settings.ISettingsReader
import com.chebuso.chargetimer.settings.ISettingsWriter


class NotificationScheduler(
    activity: Activity,
    settingsProvider: ISettingsReader,
    settingsWriter: ISettingsWriter,
    resourceProvider: IResourceProvider,
) {
    private val notificatorFactory: NotificatorFactory

    init {
        notificatorFactory =
            NotificatorFactory(activity, settingsProvider, resourceProvider, settingsWriter)
    }

    fun schedule(millisToEvent: Long) {
        for (notificator in notificatorFactory.createNotificators()) {
            notificator.scheduleCarChargedNotification(millisToEvent)
        }
    }

    fun schedule(grantResults: IntArray, millisToEvent: Long) {
        val notificator = notificatorFactory.tryCreate(isPermissionsGranted(grantResults))
        notificator?.scheduleCarChargedNotification(millisToEvent)
    }
}

