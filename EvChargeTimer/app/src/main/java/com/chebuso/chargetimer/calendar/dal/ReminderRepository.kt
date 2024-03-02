package com.chebuso.chargetimer.calendar.dal

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.database.sqlite.SQLiteDoneException
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import com.chebuso.chargetimer.UserMessage.showToast

interface IReminderRepository {
    fun setReminder(eventId: Long, minutesBefore: Int)
}

@SuppressLint("MissingPermission")
class ReminderRepository(private val activity: Activity) : IReminderRepository {

    override fun setReminder(eventId: Long, minutesBefore: Int) {
        Log.d(TAG, "setReminder. eventId=$eventId")
        val cr = activity.contentResolver
        val reminderValues = createReminderValues(eventId, minutesBefore)
        try {
            cr.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
            CalendarContract.Reminders.query(cr,
                eventId,
                arrayOf(CalendarContract.Reminders.MINUTES)
            ).use { cursor ->
                if (cursor.moveToFirst()) {
                    val minutesIndex = cursor.getColumnIndex(CalendarContract.Reminders.MINUTES)
                    val minutes = cursor.getInt(minutesIndex)
                    Log.i(TAG, "calendar $minutes")
                }
            }
        } catch (ex: SQLiteDoneException) {
            showToast(activity, ex.message!!, Toast.LENGTH_LONG)
            Log.e(TAG, ex.message!!)
        }
    }

    private fun createReminderValues(eventID: Long, minutesBefore: Int): ContentValues {
        return ContentValues().apply {
            put(CalendarContract.Reminders.MINUTES, minutesBefore)
            put(CalendarContract.Reminders.EVENT_ID, eventID)
            put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        }
    }

    companion object {
        private const val TAG = "CalendarRepository"
    }
}

