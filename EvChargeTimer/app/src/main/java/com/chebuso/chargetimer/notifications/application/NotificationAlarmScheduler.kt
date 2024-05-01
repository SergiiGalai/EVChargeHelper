package com.chebuso.chargetimer.notifications.application

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.chebuso.chargetimer.shared.helpers.TimeHelper
import com.chebuso.chargetimer.shared.helpers.TimeHelper.formatAsMediumTime

class NotificationAlarmScheduler internal constructor(
    private val context: Context
) {

    fun schedule(alarmItem: AlarmItem) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = createPendingIntent(alarmItem)

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            alarmItem.triggerAtMillis,
            intent
        )

        val date = TimeHelper.toDate(alarmItem.triggerAtMillis).formatAsMediumTime()
        Log.i(TAG, "scheduleNotification triggerAt=$date")
    }

    private fun createPendingIntent(alarmItem: AlarmItem): PendingIntent {
        val intent = Intent(context, CarChargedAlarmReceiver::class.java)
            .apply {
                putExtra(CarChargedAlarmReceiver.ALARM_ITEM, alarmItem)
            }

        return PendingIntent.getBroadcast(
            context,
            alarmItem.notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        private val TAG = NotificationAlarmScheduler::class.java.simpleName
    }
}