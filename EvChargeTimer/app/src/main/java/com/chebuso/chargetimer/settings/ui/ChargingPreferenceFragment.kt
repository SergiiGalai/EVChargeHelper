package com.chebuso.chargetimer.settings.ui

import android.os.Bundle
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.UserMessage
import com.chebuso.chargetimer.helpers.asDecimalNumber
import com.chebuso.chargetimer.helpers.asNumber
import com.chebuso.chargetimer.helpers.onPreferenceChange
import com.chebuso.chargetimer.helpers.trimNonPrintable


class ChargingPreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_charging, rootKey)

        for (preference in arrayOf<EditTextPreference?>(
            findPreference("charging_loss"),
            findPreference("default_voltage"),
            findPreference("default_amperage")
        )) {
            preference?.asNumber()?.onPreferenceChange(notEmptyValueChangeListener)
        }

        findPreference<EditTextPreference>("battery_capacity").apply {
            this?.asDecimalNumber()?.onPreferenceChange(notEmptyValueChangeListener)
        }
    }

    private val notEmptyValueChangeListener =
        Preference.OnPreferenceChangeListener { preference, value ->
            val trimmedValue = value.toString().trimNonPrintable()
            if (trimmedValue == "" || trimmedValue == "0") {
                UserMessage.showToast(preference.context, R.string.value_not_empty, Toast.LENGTH_LONG)
                return@OnPreferenceChangeListener false
            }
            true
        }
}