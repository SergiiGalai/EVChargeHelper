package com.chebuso.chargetimer.settings.ui

import android.os.Bundle
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.UserMessage
import com.chebuso.chargetimer.helpers.asNumber
import com.chebuso.chargetimer.helpers.onPreferenceChange
import com.chebuso.chargetimer.helpers.toFallbackInt
import com.chebuso.chargetimer.helpers.trimNonPrintable


class NotificationPreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_notification, rootKey)

        for (preference in arrayOf<EditTextPreference?>(
            findPreference("calendar_permission_reminder_minutes"),
            findPreference("app_notification_reminder_minutes")
        )) {
            preference?.asNumber()?.onPreferenceChange(maxValueValidationChangeListener)
        }
    }

    private val maxValueValidationChangeListener =
        Preference.OnPreferenceChangeListener { preference, newValue ->
            val newValueInt = newValue.toString().trimNonPrintable().toFallbackInt()

            if (newValueInt > MAX_REMINDER_MINUTES) {
                val context = preference.context
                val errorText = context.getString(R.string.pref_title_reminder_validation_error)
                val formattedError = String.format(errorText, MAX_REMINDER_MINUTES)
                UserMessage.showToast(context, formattedError, Toast.LENGTH_LONG)

                return@OnPreferenceChangeListener false
            }
            true
        }

    companion object {
        private const val MAX_REMINDER_MINUTES = 480
    }
}