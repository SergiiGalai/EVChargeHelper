package com.chebuso.chargetimer.helpers

import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceManager


fun EditTextPreference.asNumber(): EditTextPreference{
    this.setOnBindEditTextListener {
        it.inputType = InputType.TYPE_CLASS_NUMBER
        it.selectAll()
    }
    return this
}

fun EditTextPreference.asDecimalNumber(): EditTextPreference{
    this.setOnBindEditTextListener {
        it.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        it.selectAll()
    }
    return this
}

fun Preference.onPreferenceChange(changeListener: OnPreferenceChangeListener){
    changeListener.onPreferenceChange(this, this.getStringValueOrEmpty())
    this.onPreferenceChangeListener = changeListener
}

fun Preference.getStringValueOrEmpty(): String? = PreferenceManager
    .getDefaultSharedPreferences(this.context)
    .getString(this.key, "")

