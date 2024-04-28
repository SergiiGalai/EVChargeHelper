package com.chebuso.chargetimer.notifications.calendar


import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.helpers.TimeHelper
import com.chebuso.chargetimer.helpers.TimeHelper.toDate
import com.chebuso.chargetimer.notifications.INotificator
import java.util.Calendar
import java.util.Date


class CalendarDefaultNotificator internal constructor(
    private val context: Context
) : INotificator {

    override fun scheduleCarChargedNotification(millisToEvent: Long) {
        Log.d(TAG, "scheduleCarChargedNotification")
        val eventTime = getChargedAtDate(millisToEvent)
        val intent = getInsertIntent(
            context.getString(R.string.car_charged_title),
            context.getString(R.string.car_charged_descr),
            getCalendar(eventTime)
        )
        scheduleCalendarEvent(intent)
    }

    private fun getInsertIntent(title: String, description: String, beginTime: Calendar): Intent {
        return Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
            .putExtra(CalendarContract.Events.TITLE, title)
            .putExtra(CalendarContract.Events.DESCRIPTION, description)
    }

    private fun getCalendar(eventTime: Date): Calendar =
        Calendar.getInstance().apply {
            setTime(eventTime)
        }

    private fun getChargedAtDate(millisToEvent: Long) = toDate(getChargedAtMillis(millisToEvent))

    private fun getChargedAtMillis(millisToEvent: Long): Long = TimeHelper.now() + millisToEvent

    private fun scheduleCalendarEvent(intent: Intent) {
        context.startActivity(intent)
        Log.i(TAG, "scheduleCalendarEvent")
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}

