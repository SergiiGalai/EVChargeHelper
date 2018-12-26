package com.sergiigalai.chargetimer.notifications;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;

import com.sergiigalai.chargetimer.R;
import com.sergiigalai.chargetimer.helpers.TimeHelper;

import java.util.Calendar;
import java.util.Date;

public class GoogleCalendarDefaultNotificator implements INotificator
{
    private final Context context;

    GoogleCalendarDefaultNotificator(Context context) {
        this.context = context;
    }

    @Override
    public void scheduleCarChargedNotification(long millisToEvent) {
        scheduleCalendarEvent(context.getString(R.string.car_charged_title),
                context.getString(R.string.car_charged_descr),
                getCalendar(millisToEvent));
    }

    @NonNull
    private Calendar getCalendar(long millisToEvent) {
        Date eventTime = TimeHelper.toDate(TimeHelper.now() + millisToEvent);
        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(eventTime);
        return beginTime;
    }

    private void scheduleCalendarEvent(String title, String description, Calendar beginTime) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, description);

        context.startActivity(intent);
    }
}
