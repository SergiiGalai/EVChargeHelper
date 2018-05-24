package com.example.dell.chargehelper.notifications;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;

import com.example.dell.chargehelper.R;
import com.example.dell.chargehelper.TimeHelper;

import java.util.Date;

public class CarChargedAlarmScheduler implements ICarChargedNotificationScheduler
{
    private Activity context;
    private final Uri NotificationSound;
    private static final int REMINDER_MINUTES = 10;

    public CarChargedAlarmScheduler(Activity context) {
        this.context = context;
        NotificationSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.carhorn4);
    }

    @Override
    public void scheduleNotification(long eventTime) {
        long beforeEvent = getReminderMs(eventTime);
        long alarmIn = SystemClock.elapsedRealtime() + beforeEvent;
        String description = getNotificationDescription(eventTime);

        Notification notification = getCarChargedNotification(
                context.getString(R.string.car_charged_title),
                description
        );

        PendingIntent pendingIntent = getCarChargedNotificationPendingIntent(notification);
        scheduleNotification(alarmIn, pendingIntent);

        notifyUser(description);
    }

    private int getReminderMinutes(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(preferences.getString("app_notification_reminder_minutes", String.valueOf(REMINDER_MINUTES)));
    }

    long getReminderMs(long msToEvent) {
        long beforeEvent = TimeHelper.convertMinutesToMs(getReminderMinutes());
        if (msToEvent > beforeEvent)
            return msToEvent - beforeEvent;
        return msToEvent;
    }

    private void notifyUser(String description) {
        Snackbar.make(context.findViewById(android.R.id.content), description, Snackbar.LENGTH_LONG)
                .show();

        //Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
    }

    private String getNotificationDescription(long duration) {
        Date chargedAtTime = TimeHelper.toDate(TimeHelper.addToNow(duration));
        String descriptionTemplate = context.getString(R.string.car_charged_time_desc);
        return String.format(descriptionTemplate, TimeHelper.toHoursAndMinutes(chargedAtTime));
    }

    private Notification getCarChargedNotification(String title, String description) {
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(description)
                .setSound(NotificationSound)
                .setSmallIcon(R.mipmap.ic_launcher);

        return builder.build();
    }

    private void scheduleNotification(long triggerAt, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent);
    }

    private PendingIntent getCarChargedNotificationPendingIntent(Notification notification) {
        Intent notificationIntent = new Intent(context, CarChargedAlarmReceiver.class);
        notificationIntent.putExtra(CarChargedAlarmReceiver.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(CarChargedAlarmReceiver.NOTIFICATION, notification);
        return PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
