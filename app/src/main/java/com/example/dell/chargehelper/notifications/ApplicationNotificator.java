package com.example.dell.chargehelper.notifications;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import com.example.dell.chargehelper.R;
import com.example.dell.chargehelper.SettingsProvider;
import com.example.dell.chargehelper.helpers.TimeHelper;
import com.example.dell.chargehelper.UserMessage;

import java.util.Date;

public class ApplicationNotificator implements INotificator
{
    private final Activity activity;
    private final Uri SoundUri;

    ApplicationNotificator(Activity activity) {
        this.activity = activity;
        SoundUri = Uri.parse("android.resource://" + activity.getPackageName() + "/" + R.raw.carhorn4);
    }

    @Override
    public void scheduleCarChargedNotification(long millisToEvent) {
        long millisToNotify = getMillisToNotify(millisToEvent);
        String description = getNotificationDescription(millisToEvent);

        Notification notification = getCarChargedNotification(
            activity.getString(R.string.car_charged_title),
            description
        );

        PendingIntent pendingIntent = getNotificationIntent(notification);
        scheduleNotification(millisToNotify, pendingIntent);
        UserMessage.showSnackbar(activity, description);
    }

    private long getMillisToNotify(long millisToEvent) {
        SettingsProvider settingsProvider = new SettingsProvider(activity);
        long millisToNotify = TimeHelper.convertMinutesToMs(settingsProvider.getApplicationReminderMinutes());
        if (millisToEvent > millisToNotify)
            return millisToEvent - millisToNotify;
        return millisToEvent;
    }

    private String getNotificationDescription(long duration) {
        Date chargedAtTime = TimeHelper.toDate(TimeHelper.addToNow(duration));
        String descriptionTemplate = activity.getString(R.string.car_charged_time_desc);
        return String.format(descriptionTemplate, TimeHelper.formatAsHoursWithMinutes(chargedAtTime));
    }

    private static final String CAR_CHARGED_NOTIFICATION_CHANNEL_ID = "46578";
    private Notification getCarChargedNotification(String title, String description) {
        Notification.Builder builder = new Notification.Builder(activity)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_timer_24px)
                .setSound(SoundUri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = getNotificationChannel();
            registerNotificationChannel(channel);

            builder.setChannelId(CAR_CHARGED_NOTIFICATION_CHANNEL_ID);
        }

        return builder.build();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    private NotificationChannel getNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CAR_CHARGED_NOTIFICATION_CHANNEL_ID,
                activity.getString(R.string.car_charged_title),
                NotificationManager.IMPORTANCE_DEFAULT);

        channel.setDescription(activity.getString(R.string.car_charged_channel_descr));
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        channel.setSound(SoundUri, attributes);
        return channel;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void registerNotificationChannel(NotificationChannel channel) {
        NotificationManager notificationManager = (NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;

        notificationManager.createNotificationChannel(channel);
    }

    private PendingIntent getNotificationIntent(Notification notification) {
        Intent notificationIntent = new Intent(activity, CarChargedAlarmReceiver.class);
        notificationIntent.putExtra(CarChargedAlarmReceiver.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(CarChargedAlarmReceiver.NOTIFICATION, notification);
        return PendingIntent.getBroadcast(activity, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void scheduleNotification(long millis, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
        long triggerAt = TimeHelper.addToNow(millis);

        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
    }
}
