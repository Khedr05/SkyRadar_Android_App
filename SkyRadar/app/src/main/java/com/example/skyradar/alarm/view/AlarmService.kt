package com.example.skyradar.alarm.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.skyradar.R

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AlarmService", "onStartCommand called")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            val notification = NotificationCompat.Builder(this, "ALARM_SERVICE_CHANNEL")
                .setContentTitle("Alarm is Running")
                .setContentText("Alarm sound is playing")
                .setSmallIcon(R.drawable.home)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
            startForeground(2, notification)
        }

        // Initialize and start MediaPlayer
        if (mediaPlayer == null) {
            Log.d("AlarmService", "Initializing MediaPlayer")
            try {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    setDataSource(applicationContext, android.net.Uri.parse("android.resource://${packageName}/${R.raw.alarm_sound}"))
                    isLooping = true
                    prepare()
                    start()
                    Log.d("AlarmService", "MediaPlayer started successfully")
                }
            } catch (e: Exception) {
                Log.e("AlarmService", "Error initializing MediaPlayer", e)
            }
        } else {
            Log.d("AlarmService", "MediaPlayer already initialized")
        }

        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("AlarmService", "onDestroy called, stopping MediaPlayer")
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
                Log.d("AlarmService", "MediaPlayer stopped")
            }
            release()
        }
        mediaPlayer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "ALARM_SERVICE_CHANNEL",
                "Alarm Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
