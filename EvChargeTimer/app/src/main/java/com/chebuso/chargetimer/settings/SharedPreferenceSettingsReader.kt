package com.chebuso.chargetimer.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.chebuso.chargetimer.R

class SharedPreferenceSettingsReader(context: Context): ISettingsReader {
    private val context: Context
    private val preferences: SharedPreferences

    init {
        this.context = context
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun calendarBasicNotificationsAllowed(): Boolean {
        val default = context.resources.getBoolean(R.bool.pref_default_allow_calendar_notifications)
        return preferences.getBoolean("allow_calendar_notifications", default)
    }

    override fun calendarAdvancedNotificationsAllowed(): Boolean {
        return calendarBasicNotificationsAllowed() &&
                preferences.getBoolean("allow_calendar_permission_notifications",
                    context.resources.getBoolean(R.bool.pref_default_allow_calendar_permission_notifications));
    }

    override fun getCalendarReminderMinutes(): Int {
        val default = context.getString(R.string.pref_default_calendar_permission_reminder_minutes)
        return parseInteger("calendar_permission_reminder_minutes", default)
    }

    override fun getDefaultAmperage(): Int {
        val default = context.getString(R.string.pref_default_amperage)
        return parseInteger("default_amperage", default)
    }

    override fun getDefaultVoltage(): Int {
        val default = context.getString(R.string.pref_default_voltage)
        return parseInteger("default_voltage", default)
    }

    override fun applicationNotificationsAllowed(): Boolean {
        val default = context.resources.getBoolean(R.bool.pref_default_allow_app_notifications)
        return preferences.getBoolean("allow_app_notifications", default)
    }

    override fun getApplicationReminderMinutes(): Int {
        val default = context.getString(R.string.pref_default_app_notification_reminder_minutes)
        return parseInteger("app_notification_reminder_minutes", default)
    }

    override fun getBatteryCapacity(): Double {
        val default = context.getString(R.string.pref_default_battery_capacity)
        return parseDouble("battery_capacity", default)
    }

    override fun getChargingLossPct(): Double {
        val default = context.getString(R.string.pref_default_charging_loss)
        return parseDouble("charging_loss", default)
    }

    private fun parseInteger(key: String, valueWhenEmpty: String): Int {
        val value = preferences.getString(key, valueWhenEmpty)
        return (if (value.isNullOrEmpty()) valueWhenEmpty else value).toInt()
    }

    private fun parseDouble(key: String, valueWhenEmpty: String): Double {
        val value = preferences.getString(key, valueWhenEmpty)
        return (if (value.isNullOrEmpty()) valueWhenEmpty else value).toDouble()
    }

    override fun firstApplicationRun(): Boolean {
        return false
        //return preferences.getBoolean("first_application_run", true)
    }
}