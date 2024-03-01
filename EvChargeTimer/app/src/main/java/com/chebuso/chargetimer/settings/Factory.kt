package com.chebuso.chargetimer.settings

import android.content.Context

object Factory {
    fun createSettingsReader(context: Context): ISettingsReader {
        return SharedPreferenceSettingsReader(context)
    }

    fun createSettingsWriter(context: Context): ISettingsWriter{
        return SharedPreferenceSettingsWriter(context)
    }
}