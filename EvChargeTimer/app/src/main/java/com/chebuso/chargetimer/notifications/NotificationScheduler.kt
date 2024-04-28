package com.chebuso.chargetimer.notifications

class NotificationScheduler(
    private val notificatorFactory: NotificatorFactory
) {

    fun schedule(millisToEvent: Long) {
        val notificators = notificatorFactory.createNotificators()
        for (notificator in notificators) {
            notificator.scheduleCarChargedNotification(millisToEvent)
        }
    }

    fun schedule(grantResult: Boolean, millisToEvent: Long) {
        val notificator = notificatorFactory.tryCreate(grantResult)
        notificator?.scheduleCarChargedNotification(millisToEvent)
    }
}

