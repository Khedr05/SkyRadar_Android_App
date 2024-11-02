package com.example.skyradar.alarm.view

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.skyradar.R
import java.util.*

class AlarmFragment : Fragment(R.layout.fragment_alarm) {

    private lateinit var timePicker: TimePicker
    private lateinit var setAlarmButton: Button
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timePicker = view.findViewById(R.id.timePicker)
        setAlarmButton = view.findViewById(R.id.setAlarmButton)
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Set default time for TimePicker
        val calendar = Calendar.getInstance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
            timePicker.minute = calendar.get(Calendar.MINUTE)
        } else {
            timePicker.currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            timePicker.currentMinute = calendar.get(Calendar.MINUTE)
        }

        setAlarmButton.setOnClickListener {
            val selectedHour: Int
            val selectedMinute: Int

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                selectedHour = timePicker.hour
                selectedMinute = timePicker.minute
            } else {
                selectedHour = timePicker.currentHour
                selectedMinute = timePicker.currentMinute
            }

            val alarmTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
                set(Calendar.SECOND, 0)
            }

            setAlarm(alarmTime.timeInMillis)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(timeInMillis: Long) {
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        Toast.makeText(requireContext(), "Alarm set!", Toast.LENGTH_SHORT).show()
    }
}
