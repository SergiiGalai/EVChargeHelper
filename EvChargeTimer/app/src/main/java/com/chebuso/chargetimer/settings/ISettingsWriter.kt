package com.chebuso.chargetimer.settings

interface ISettingsWriter {
    fun setFirstApplicationRunCompleted()
    fun saveCalendarAdvancedNotificationsAllowed(value: Boolean)
}

