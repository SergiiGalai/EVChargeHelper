package com.chebuso.chargetimer.calendar.dal

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.graphics.Color
import android.provider.CalendarContract
import android.util.Log
import com.chebuso.chargetimer.calendar.CalendarEntity

interface ICalendarRepository {
    fun getPrimaryCalendar(): CalendarEntity?
    fun getAvailableCalendars(): List<CalendarEntity>
    fun createCalendar(calendar: CalendarEntity, calendarColor: String?): Long
    fun deleteCalendar(calendarName: String): Int
}

@SuppressLint("MissingPermission")
class CalendarRepository(private val activity: Activity) : ICalendarRepository {
    private val entityReader = CalendarEntityReader()

    override fun getPrimaryCalendar(): CalendarEntity? {
        Log.d(TAG, "getPrimaryCalendar")
        val calendars = ArrayList<CalendarEntity>()
        activity.contentResolver
            .query(
                CalendarContract.Calendars.CONTENT_URI,
                null, null, null, null
            )?.use {
                while (it.moveToNext()) {
                    val calendar = entityReader.fromCursorPosition(it)
                    if (calendar.isPrimary) {
                        Log.d(TAG, "getPrimaryCalendar. Found id=" + calendar.id)
                        return calendar
                    }
                    calendars.add(calendar)
                }
            }

        Log.d(TAG, "getPrimaryCalendar. Circle through the calendar list")
        for (calendar in calendars) {
            if (calendar.isPrimaryAlternative) {
                Log.d(TAG, "getPrimaryCalendar. Found id=" + calendar.id)
                return calendar
            }
        }

        return null
    }

    override fun getAvailableCalendars(): List<CalendarEntity> {
        Log.d(TAG, "getAvailableCalendars")
        val result = ArrayList<CalendarEntity>()

        activity.contentResolver
            .query(
                CalendarContract.Calendars.CONTENT_URI,
                null, null, null, null
            )?.use {
                while (it.moveToNext()) {
                    val calendar = entityReader.fromCursorPosition(it)
                    result.add(calendar)
                }
            }

        return result
    }

    override fun deleteCalendar(calendarName: String): Int {
        Log.d(TAG, "deleteCalendar $calendarName")
        val id = getCalendarId(calendarName)
        if (id == DOES_NOT_EXIST)
            return 0

        val calUri = CalendarContract.Calendars.CONTENT_URI
        val deleteUri = ContentUris.withAppendedId(calUri, id)
        return activity.contentResolver.delete(deleteUri, null, null)
    }

    override fun createCalendar(calendar: CalendarEntity, calendarColor: String?): Long {
        try {
            Log.d(TAG, "createCalendar " + calendar.displayName)

            // don't create if it already exists
            val id = getCalendarId(calendar.displayName)
            if (id != DOES_NOT_EXIST)
                return id

            // doesn't exist yet, so create
            var calUri = CalendarContract.Calendars.CONTENT_URI
            val cv = ContentValues()
            cv.put(CalendarContract.Calendars.ACCOUNT_NAME, calendar.accountName)
            cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
            cv.put(CalendarContract.Calendars.NAME, calendar.displayName)
            cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, calendar.displayName)
            if (calendarColor != null) {
                val colorInt = Color.parseColor(calendarColor)
                cv.put(CalendarContract.Calendars.CALENDAR_COLOR, colorInt)
            }
            cv.put(CalendarContract.Calendars.VISIBLE, 1)
            cv.put(
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.CAL_ACCESS_OWNER
            )
            cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, calendar.accountName)
            cv.put(CalendarContract.Calendars.SYNC_EVENTS, 0)
            calUri = calUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, calendar.accountName)
                .appendQueryParameter(
                    CalendarContract.Calendars.ACCOUNT_TYPE,
                    CalendarContract.ACCOUNT_TYPE_LOCAL
                )
                .build()
            val contentResolver = activity.contentResolver
            val created = contentResolver.insert(calUri, cv)
            if (created != null) {
                val lastSegment = created.lastPathSegment
                if (lastSegment != null) return lastSegment.toLong()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Creating calendar failed.", e)
        }
        return DOES_NOT_EXIST
    }

    private fun getCalendarId(calendarName: String?): Long {
        val uri = CalendarContract.Calendars.CONTENT_URI
        val contentResolver = activity.contentResolver
        contentResolver.query(
            uri, arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
            ), null, null, null
        )?.use {
            while (it.moveToNext()) {
                val id = it.getLong(0)
                val name = it.getString(1)
                val displayName = it.getString(2)
                if (
                    name != null && name == calendarName ||
                    displayName != null && displayName == calendarName
                ) return id
            }
        }
        return DOES_NOT_EXIST
    }

    companion object {
        private const val TAG = "CalendarRepository"
        private const val DOES_NOT_EXIST: Long = -1
    }
}

