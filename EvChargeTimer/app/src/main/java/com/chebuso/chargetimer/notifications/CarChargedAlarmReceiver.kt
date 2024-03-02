package com.chebuso.chargetimer.notifications

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class CarChargedAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive")
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = intent.getParcelableExtra<Notification>(NOTIFICATION)
        val id = intent.getIntExtra(NOTIFICATION_ID, 0)
        notificationManager.notify(id, notification)
        Log.i(TAG, "Sent notification to the user")
    }

    companion object {
        const val NOTIFICATION_ID = "car-charged-notification-id"
        const val NOTIFICATION = "notification"
        private val TAG = CarChargedAlarmReceiver::class.java.simpleName
    }
}

