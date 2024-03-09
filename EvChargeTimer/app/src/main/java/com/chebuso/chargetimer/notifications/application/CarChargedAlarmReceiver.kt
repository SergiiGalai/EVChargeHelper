package com.chebuso.chargetimer.notifications.application

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.chebuso.chargetimer.Factory
import com.chebuso.chargetimer.UserMessage
import com.chebuso.chargetimer.helpers.TimeHelper
import com.chebuso.chargetimer.helpers.parcelable

class CarChargedAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive")
        val alarmItem = intent.parcelable<AlarmItem>(ALARM_ITEM)

        if (alarmItem?.notificationChannelId == null){
            UserMessage.showToast(context,
                "Cannot set up notification because of unexpected error. Try using calendar notifications")
        }else{
            notify(context, alarmItem)
        }
    }

    private fun notify(context: Context, alarmItem: AlarmItem) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationFactory = Factory.notificationFactory(context)

        val notification = notificationFactory.carChargedNotification(
            TimeHelper.toDate(alarmItem.chargedAtMillis),
            alarmItem.notificationChannelId!!
        )
        notificationManager.notify(alarmItem.notificationId, notification)
        Log.i(TAG, "Notification sent: item=${alarmItem}")
    }

    companion object {
        const val ALARM_ITEM = "car-charged-trigger-AlarmItem"
        private val TAG = CarChargedAlarmReceiver::class.java.simpleName
    }
}

