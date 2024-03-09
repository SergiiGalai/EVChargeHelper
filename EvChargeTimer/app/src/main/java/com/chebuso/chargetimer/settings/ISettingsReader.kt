package com.chebuso.chargetimer.settings


interface ISettingsReader {
    fun calendarBasicNotificationsAllowed(): Boolean
    fun calendarAdvancedNotificationsAllowed(): Boolean
    fun getCalendarReminderMinutes(): Int
    fun getDefaultAmperage(): Int
    fun getDefaultVoltage(): Int
    fun applicationNotificationsAllowed(): Boolean
    fun getApplicationReminderMinutes(): Int
    fun getBatteryCapacity(): Double
    fun getChargingLossPct(): Double
    fun firstApplicationRun(): Boolean
}

