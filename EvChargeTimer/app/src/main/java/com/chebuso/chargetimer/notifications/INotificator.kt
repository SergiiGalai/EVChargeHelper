package com.chebuso.chargetimer.notifications


interface INotificator {
    fun scheduleCarChargedNotification(millisToEvent: Long)
}