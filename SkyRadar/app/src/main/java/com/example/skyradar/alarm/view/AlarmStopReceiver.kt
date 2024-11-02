package com.example.skyradar.alarm.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat

class AlarmStopReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmStopReceiver", "Stopping AlarmService")

        // Stop AlarmService, which will stop the alarm sound
        context.stopService(Intent(context, AlarmService::class.java))

        // Cancel notification
        NotificationManagerCompat.from(context).cancel(1)
    }
}
