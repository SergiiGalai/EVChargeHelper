package com.chebuso.chargetimer.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SharedPreferenceSettingsWriter(context: Context): ISettingsWriter {
    private val context: Context
    private val preferences: SharedPreferences

    init {
        this.context = context
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun setFirstApplicationRunCompleted() {
        preferences.edit().apply{
            putBoolean("first_application_run", false)
        }.apply()
    }

    override fun saveCalendarAdvancedNotificationsAllowed(value: Boolean) {
        preferences.edit().apply{
            putBoolean("allow_calendar_permission_notifications", value)
        }.apply()
    }
}