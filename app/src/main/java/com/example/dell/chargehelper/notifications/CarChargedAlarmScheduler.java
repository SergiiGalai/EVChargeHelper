package com.example.dell.chargehelper.notifications;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;

import com.example.dell.chargehelper.R;
import com.example.dell.chargehelper.TimeHelper;

import java.util.Date;

public class CarChargedAlarmScheduler implements ICarChargedNotificationScheduler
{
    private Activity activity;
    private final Uri NotificationSound;
    private static final int REMINDER_MINUTES = 10;

    public CarChargedAlarmScheduler(Activity activity) {
        this.activity = activity;
        NotificationSound = Uri.parse("android.resource://" + activity.getPackageName() + "/" + R.raw.carhorn4);
    }

    @Override
    public void scheduleNotification(long eventTime) {
        long msBeforeEvent = getReminderMs(eventTime);
        long alarmAt = SystemClock.elapsedRealtime() + msBeforeEvent;
        String description = getNotificationDescription(eventTime);

        Notification notification = getCarChargedNotification(
                activity.getString(R.string.car_charged_title),
                description
        );

        PendingIntent pendingIntent = getCarChargedNotificationPendingIntent(notification);
        scheduleNotification(alarmAt, pendingIntent);

        notifyUser(description);
    }

    private int getReminderMinutes(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return Integer.parseInt(preferences.getString("app_notification_reminder_minutes", String.valueOf(REMINDER_MINUTES)));
    }

    long getReminderMs(long msToEvent) {
        long beforeEvent = TimeHelper.convertMinutesToMs(getReminderMinutes());
        if (msToEvent > beforeEvent)
            return msToEvent - beforeEvent;
        return msToEvent;
    }

    private void notifyUser(String description) {
        Snackbar.make(activity.findViewById(android.R.id.content), description, Snackbar.LENGTH_LONG)
                .show();

        //Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
    }

    private String getNotificationDescription(long duration) {
        Date chargedAtTime = TimeHelper.toDate(TimeHelper.addToNow(duration));
        String descriptionTemplate = activity.getString(R.string.car_charged_time_desc);
        return String.format(descriptionTemplate, TimeHelper.toHoursAndMinutes(chargedAtTime));
    }

    private static final String CAR_CHARGED_NOTIFICATION_CHANNEL_ID = "46578";
    private Notification getCarChargedNotification(String title, String description) {
        Notification.Builder builder = new Notification.Builder(activity)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_timer_24px);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CAR_CHARGED_NOTIFICATION_CHANNEL_ID,
                    title, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(activity.getString(R.string.car_charged_channel_descr));
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setSound(NotificationSound, null);

            NotificationManager notificationManager = (NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.deleteNotificationChannel(CAR_CHARGED_NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(channel);

            builder.setChannelId(CAR_CHARGED_NOTIFICATION_CHANNEL_ID);
        }else{
            builder.setSound(NotificationSound);
        }

        return builder.build();
    }

    private void scheduleNotification(long triggerAt, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent);
    }

    private PendingIntent getCarChargedNotificationPendingIntent(Notification notification) {
        Intent notificationIntent = new Intent(activity, CarChargedAlarmReceiver.class);
        notificationIntent.putExtra(CarChargedAlarmReceiver.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(CarChargedAlarmReceiver.NOTIFICATION, notification);
        return PendingIntent.getBroadcast(activity, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
