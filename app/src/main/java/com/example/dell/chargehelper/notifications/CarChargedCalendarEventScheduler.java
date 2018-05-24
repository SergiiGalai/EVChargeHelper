package com.example.dell.chargehelper.notifications;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;

import com.example.dell.chargehelper.R;
import com.example.dell.chargehelper.TimeHelper;

import java.util.Calendar;
import java.util.Date;

public class CarChargedCalendarEventScheduler implements ICarChargedNotificationScheduler
{
    private Context context;

    public CarChargedCalendarEventScheduler(Context context) {
        this.context = context;
    }

    @Override
    public void scheduleNotification(long duration) {
        Date eventTime = TimeHelper.toDate(TimeHelper.addToNow(duration));
        scheduleCalendarEvent(context.getString(R.string.car_charged_title),
                context.getString(R.string.car_charged_descr),
                eventTime);
    }

    private void scheduleCalendarEvent(String title, String description, Date eventTime) {
        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(eventTime);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, description)
                ;

        context.startActivity(intent);
    }
}
