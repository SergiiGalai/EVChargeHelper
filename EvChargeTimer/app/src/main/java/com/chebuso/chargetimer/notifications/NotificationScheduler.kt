package com.chebuso.chargetimer.notifications

class NotificationScheduler(
    private val notificatorFactory: NotificatorFactory
) {
    fun scheduleAll(millisToEvent: Long) {
        notificatorFactory.createNotificators().forEach {
            it.scheduleCarChargedNotification(millisToEvent)
        }
    }

    fun scheduleCalendar(permissionsGranted: Boolean, millisToEvent: Long) {
        val notificator = notificatorFactory.tryCreateCalendarNotificator(permissionsGranted)
        notificator?.scheduleCarChargedNotification(millisToEvent)
    }
}

