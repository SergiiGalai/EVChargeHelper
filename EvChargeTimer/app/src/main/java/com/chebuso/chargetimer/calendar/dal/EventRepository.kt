package com.chebuso.chargetimer.calendar.dal

import android.app.Activity
import android.content.ContentValues
import android.database.sqlite.SQLiteDoneException
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import com.chebuso.chargetimer.UserMessage.showToast
import com.chebuso.chargetimer.calendar.CalendarEventEntity
import com.chebuso.chargetimer.helpers.TimeHelper.now
import com.chebuso.chargetimer.helpers.emptyIfNull
import com.chebuso.chargetimer.helpers.toFallbackLong
import java.util.Calendar

interface IEventRepository {
    /**
     * @return event id. If not created returns -1
     */
    fun createEvent(calendarId: Long, event: CalendarEventEntity?): Long
}

class EventRepository(private val activity: Activity) : IEventRepository {

    override fun createEvent(calendarId: Long, event: CalendarEventEntity?): Long {
        Log.d(TAG, "createEvent. calendarId=$calendarId")
        val cr = activity.contentResolver
        return try {
            val values = createCalendarEventContent(calendarId, event)
            val uri = cr.insert(CalendarContract.Events.CONTENT_URI, values)
            val eventId = if (uri == null) "" else uri.lastPathSegment.emptyIfNull()
            eventId.toFallbackLong(DOES_NOT_EXIST)
        } catch (ex: SQLiteDoneException) {
            val message = ex.message ?: "Unknown SQL exception happened: $ex"
            showToast(activity, message, Toast.LENGTH_LONG)
            Log.e(TAG, message)
            DOES_NOT_EXIST
        }
    }


    private fun customColorsSupported(): Boolean {
        Log.d(TAG, "customColorsSupported")
        activity.contentResolver
            .query(
                CalendarContract.Colors.CONTENT_URI, arrayOf(
                    CalendarContract.Colors.COLOR_KEY,
                    CalendarContract.Colors.COLOR
                ),
                null, null, null
            )?.use {
                val result = it.moveToFirst()
                Log.d(
                    TAG,
                    "customColorsSupported. result=$result"
                )
                return result
            }
        return false
    }

    private fun createCalendarEventContent(
        calendarId: Long,
        event: CalendarEventEntity?
    ): ContentValues {
        val eventTime = now() + event!!.millisToStart
        val values = ContentValues()
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId)
        values.put(CalendarContract.Events.TITLE, event.title)
        values.put(CalendarContract.Events.DESCRIPTION, event.description)
        values.put(CalendarContract.Events.DTSTART, eventTime)
        values.put(CalendarContract.Events.DTEND, eventTime + MS_IN_1_HOUR)
        values.put(
            CalendarContract.Events.EVENT_TIMEZONE,
            Calendar.getInstance().getTimeZone().id
        )
        return values
    }

    companion object {
        private const val TAG = "EventRepository"
        private const val MS_IN_1_HOUR = 60 * 60 * 1000
        private const val DOES_NOT_EXIST: Long = -1
    }
}

