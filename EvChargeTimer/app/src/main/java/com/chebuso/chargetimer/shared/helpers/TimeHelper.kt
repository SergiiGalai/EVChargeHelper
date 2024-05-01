package com.chebuso.chargetimer.shared.helpers

import android.util.Log
import com.chebuso.chargetimer.shared.Time
import java.text.DateFormat
import java.util.Date
import java.util.concurrent.TimeUnit


object TimeHelper {
    private const val TAG = "TimeHelper"

    fun Date.formatAsShortDateTime(): String {
        Log.d(TAG, "formatAsShortDateTime")
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(this)
    }

    fun Date.formatAsShortTime(): String {
        Log.d(TAG, "formatAsShortTime")
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(this)
    }

    fun Date.formatAsMediumTime(): String {
        Log.d(TAG, "formatAsMediumTime")
        return DateFormat.getTimeInstance(DateFormat.MEDIUM).format(this)
    }

    fun toTime(millis: Long): Time {
        Log.d(TAG, "toTime")
        return Time(
            TimeUnit.MILLISECONDS.toDays(millis).toInt(),
            TimeUnit.MILLISECONDS.toHours(millis).toInt() % 24,
            TimeUnit.MILLISECONDS.toMinutes(millis).toInt() % 60
        )
    }

    fun toDate(millis: Long): Date {
        Log.d(TAG, "toDate")
        return Date(millis)
    }

    fun now(): Long = System.currentTimeMillis()

    fun convertMinutesToMs(minutes: Int): Long {
        Log.d(TAG, "convertMinutesToMs")
        require(minutes >= 0) { "minutes must be positive" }
        return (minutes * 60 * 1000).toLong()
    }
}

