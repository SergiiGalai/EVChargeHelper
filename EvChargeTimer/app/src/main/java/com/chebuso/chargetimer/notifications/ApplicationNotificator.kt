package com.chebuso.chargetimer.notifications


import android.app.Activity
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.util.Log
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.UserMessage.showSnackbar
import com.chebuso.chargetimer.helpers.TimeHelper
import com.chebuso.chargetimer.helpers.TimeHelper.formatAsShortTime
import com.chebuso.chargetimer.helpers.TimeHelper.toDate
import com.chebuso.chargetimer.settings.ISettingsReader
import java.util.Date


class ApplicationNotificator internal constructor(
    private val settingsProvider: ISettingsReader,
    private val resourceProvider: IResourceProvider,
    private val activity: Activity
) : INotificator {

    override fun scheduleCarChargedNotification(millisToEvent: Long) {
        val triggerAtMillis = getTriggerAtMillis(millisToEvent)
        val chargedAt = getChargedAt(millisToEvent)

        val description = getNotificationDescription(chargedAt, toDate(triggerAtMillis))
        val notification = getCarChargedNotification(
            activity.getString(R.string.car_charged_title),
            description
        )
        val pendingIntent = getNotificationIntent(notification)
        scheduleNotification(triggerAtMillis, pendingIntent)
        showSnackbar(activity, description)
    }

    private fun getTriggerAtMillis(millisToEvent: Long): Long{
        val millisToNotify = getMillisToNotify(millisToEvent)
        return TimeHelper.now() + millisToNotify
    }

    private fun getChargedAt(millisToEvent: Long): Date{
        return toDate(TimeHelper.now() + millisToEvent)
    }

    private fun getMillisToNotify(millisToEvent: Long): Long {
        val millisToNotify = TimeHelper.convertMinutesToMs(settingsProvider.getApplicationReminderMinutes())
        return if (millisToEvent > millisToNotify) millisToEvent - millisToNotify else millisToEvent
    }

    private fun getNotificationDescription(chargedAt: Date, triggerAt: Date): String {
        val descriptionTemplate = activity.getString(R.string.car_charged_time_desc)
        return String.format(descriptionTemplate,
            chargedAt.formatAsShortTime(),
            triggerAt.formatAsShortTime())
    }

    private fun getCarChargedNotification(title: String, description: String): Notification {
        val builder = Notification.Builder(activity, CAR_CHARGED_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_timer_24px)
            .setSound(resourceProvider.applicationNotificationSoundUri)

        registerNotificationChannel(notificationChannel)
        builder.setChannelId(CAR_CHARGED_NOTIFICATION_CHANNEL_ID)
        return builder.build()
    }

    private val notificationChannel: NotificationChannel
        get() {
            return NotificationChannel(
                CAR_CHARGED_NOTIFICATION_CHANNEL_ID,
                activity.getString(R.string.car_charged_title),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = activity.getString(R.string.car_charged_channel_descr)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setSound(resourceProvider.applicationNotificationSoundUri, soundAttributes)
            }
        }

    private val soundAttributes: AudioAttributes
        get() = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

    private fun registerNotificationChannel(channel: NotificationChannel) {
        val notificationManager =
            (activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        notificationManager.createNotificationChannel(channel)
    }

    private fun getNotificationIntent(notification: Notification): PendingIntent {
        val notificationIntent = Intent(activity, CarChargedAlarmReceiver::class.java
        ).apply {
            putExtra(CarChargedAlarmReceiver.NOTIFICATION_ID, 1)
            putExtra(CarChargedAlarmReceiver.NOTIFICATION, notification)
        }

        return PendingIntent.getBroadcast(activity,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun scheduleNotification(triggerAtMillis: Long, pendingIntent: PendingIntent) {
        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)

        val date = toDate(triggerAtMillis).formatAsShortTime()
        Log.i(TAG, "scheduleNotification triggerAt=$date")
    }

    companion object {
        private const val CAR_CHARGED_NOTIFICATION_CHANNEL_ID = "46578"
        private val TAG = ApplicationNotificator::class.java.simpleName
    }
}

