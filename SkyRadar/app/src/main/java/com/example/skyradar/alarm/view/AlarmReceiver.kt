package com.example.skyradar.alarm.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.skyradar.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm received, starting AlarmService")

        // Start AlarmService to play the alarm sound
        val alarmServiceIntent = Intent(context, AlarmService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(alarmServiceIntent)
        } else {
            context.startService(alarmServiceIntent)
        }

        // Create the notification channel
        createNotificationChannel(context)

        // Stop alarm sound on notification button click
        val stopIntent = Intent(context, AlarmStopReceiver::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create notification with "Check the weather" message and stop button
        val notification = NotificationCompat.Builder(context, "ALARM_CHANNEL_ID")
            .setSmallIcon(R.drawable.home)
            .setContentTitle("Check the weather")
            .setContentText("Click below to stop the alarm sound")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.alarm, "Stop Alarm", stopPendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1, notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Alarm Channel"
            val descriptionText = "Channel for alarm notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("ALARM_CHANNEL_ID", channelName, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
