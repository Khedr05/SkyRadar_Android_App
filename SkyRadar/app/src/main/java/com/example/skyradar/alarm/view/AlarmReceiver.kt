package com.example.skyradar.alarm.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.skyradar.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("notification_message") ?: "It's time!"

        // Wake up the screen
        wakeUpScreen(context)

        // Create notification channel for devices running Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm Notifications"
            val descriptionText = "Channel for alarm notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("alarm_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Set up the notification with sound
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(R.drawable.map)
            .setContentTitle("Alarm Notification")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri) // Set the default notification sound
            .setAutoCancel(true) // Dismiss notification when tapped

        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }

    private fun wakeUpScreen(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "skyradar:alarm_wakelock"
        )
        wakeLock.acquire(5000) // Wake screen for 5 seconds

        // Log to check if the wake lock is acquired
        Log.d("AlarmReceiver", "Wake lock acquired to wake up screen")
    }

}
