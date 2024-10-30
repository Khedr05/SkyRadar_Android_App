package com.example.skyradar.alarm.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
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
                setAlarm(alarm)
            } else {
                cancelAlarm(alarm)
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = alarmAdapter

        btnSetTime.setOnClickListener {
            showTimePicker()
        }

        checkPermissions()

        return view
    }

    private fun checkPermissions() {
        // Check Notification permission
        if (!NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
            showPermissionAlert(
                title = "Notification Permission Required",
                message = "This app requires notification permissions to alert you at the scheduled times. Would you like to enable it?",
                onPositiveAction = {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                    }
                    startActivity(intent)
                }
            )
        }

        // Check Alarm permission (only needed on Android 12 and higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            showPermissionAlert(
                title = "Exact Alarm Permission Required",
                message = "This app requires exact alarm permission to function correctly. Would you like to enable it?",
                onPositiveAction = {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    startActivity(intent)
                }
            )
        }
    }

    // Helper function to show an alert dialog for permissions
    private fun showPermissionAlert(title: String, message: String, onPositiveAction: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Enable") { _, _ -> onPositiveAction() }
            .setNegativeButton("Cancel", null)
            .show()
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
            false
        )
        timePicker.show()
    }

    private fun addAlarm(calendar: Calendar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Request the user to enable exact alarms
            showPermissionAlert(
                title = "Exact Alarm Permission Required",
                message = "This app requires exact alarm permission to function correctly. Would you like to enable it?",
                onPositiveAction = {
                    startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                }
            )
            return
        }

        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra("notification_message", "Time to check your app!")
        val requestCode = System.currentTimeMillis().toInt()

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
        alarm.isActive = true

        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra("notification_message", "Time to check your app!")

        pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alarm.requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

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
