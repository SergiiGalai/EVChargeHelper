package com.chebuso.chargetimer.notifications;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.chebuso.chargetimer.R;
import com.chebuso.chargetimer.helpers.TimeHelper;

import java.util.Calendar;
import java.util.Date;

public class CalendarDefaultNotificator implements INotificator
{
    private static final String TAG = "CalDefaultNotificator";

    private final Context context;

    CalendarDefaultNotificator(Context context) {
        this.context = context;
    }

    @Override
    public void scheduleCarChargedNotification(long millisToEvent) {
        Log.d(TAG, "scheduleCarChargedNotification");

        Intent intent = getInsertIntent(context.getString(R.string.car_charged_title),
                context.getString(R.string.car_charged_descr),
                getCalendar(millisToEvent));
        scheduleCalendarEvent(intent);
    }

    @NonNull
    private Intent getInsertIntent(String title, String description, Calendar beginTime) {
        return new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, title)
                    .putExtra(CalendarContract.Events.DESCRIPTION, description);
    }

    @NonNull
    private Calendar getCalendar(long millisToEvent) {
        Date eventTime = TimeHelper.toDate(TimeHelper.now() + millisToEvent);
        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(eventTime);
        return beginTime;
    }

    private void scheduleCalendarEvent(Intent intent) {
        context.startActivity(intent);
    }
}
