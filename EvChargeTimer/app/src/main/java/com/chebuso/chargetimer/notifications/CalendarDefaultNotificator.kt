package com.chebuso.chargetimer.notifications


import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.helpers.TimeHelper.now
import com.chebuso.chargetimer.helpers.TimeHelper.toDate
import java.util.Calendar


class CalendarDefaultNotificator internal constructor(
    private val context: Context
) : INotificator {

    override fun scheduleCarChargedNotification(millisToEvent: Long) {
        Log.d(TAG, "scheduleCarChargedNotification")
        val intent = getInsertIntent(
            context.getString(R.string.car_charged_title),
            context.getString(R.string.car_charged_descr),
            getCalendar(millisToEvent)
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

    private fun getCalendar(millisToEvent: Long): Calendar {
        val eventTime = toDate(now() + millisToEvent)
        val beginTime = Calendar.getInstance()
        beginTime.setTime(eventTime)
        return beginTime
    }

    private fun scheduleCalendarEvent(intent: Intent) {
        context.startActivity(intent)
        Log.i(TAG, "scheduleCalendarEvent")
    }

    companion object {
        private const val TAG = "CalendarDefaultNotificator"
    }
}

