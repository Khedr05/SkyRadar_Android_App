package com.example.skyradar.alarm.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.R
import java.util.Calendar

class AlarmFragment : Fragment() {

    private lateinit var btnSetTime: Button
    private lateinit var tvSelectedTime: TextView
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private val alarmList = mutableListOf<Alarm>()
    private lateinit var alarmAdapter: AlarmAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alarm, container, false)

        btnSetTime = view.findViewById(R.id.btnSetTime)
        tvSelectedTime = view.findViewById(R.id.tvSelectedTime)

        // Setup RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewAlarms)
        alarmAdapter = AlarmAdapter(requireContext(), alarmList) { alarm, isChecked ->
            if (isChecked) {
                setAlarm(alarm) // Call the function to set the alarm
            } else {
                cancelAlarm(alarm)
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = alarmAdapter

        btnSetTime.setOnClickListener {
            showTimePicker()
        }

        return view
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                val amPm = if (hourOfDay >= 12) "PM" else "AM"
                tvSelectedTime.text = "Selected time: $hour:$minute $amPm"

                addAlarm(calendar)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // Set to false for 12-hour format
        )
        timePicker.show()
    }

    private fun addAlarm(calendar: Calendar) {
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra("notification_message", "Time to check your app!")

        val requestCode = System.currentTimeMillis().toInt() // Unique request code

        pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        alarmList.add(Alarm(requestCode, calendar.timeInMillis))
        alarmAdapter.updateAlarms(alarmList)
    }

    private fun setAlarm(alarm: Alarm) {
        alarm.isActive = true // Set the alarm as active

        // Create a new PendingIntent for the alarm
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra("notification_message", "Time to check your app!")

        pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alarm.requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set the alarm again
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarm.timeInMillis,
            pendingIntent
        )
    }

    private fun cancelAlarm(alarm: Alarm) {
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alarm.requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)

        alarm.isActive = false
        alarmAdapter.updateAlarms(alarmList)
    }
}
