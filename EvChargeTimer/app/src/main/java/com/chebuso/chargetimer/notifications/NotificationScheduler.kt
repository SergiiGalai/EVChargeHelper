package com.chebuso.chargetimer.notifications

import com.chebuso.chargetimer.helpers.PermissionHelper.isPermissionsGranted


class NotificationScheduler(
    private val notificatorFactory: NotificatorFactory
) {

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

