package com.example.skyradar.alarm.view

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.R
import com.example.skyradar.alarm.viewmodel.AlarmViewModel
import com.example.skyradar.alarm.viewmodel.AlarmViewModelFactory
import com.example.skyradar.database.AlarmLocalDataSourceImpl
import com.example.skyradar.database.LocationLocalDataSourceImpl
import com.example.skyradar.favouritesLocations.viewmodel.FavouritesLocationsViewModel
import com.example.skyradar.favouritesLocations.viewmodel.FavouritesLocationsViewModelFactory
import com.example.skyradar.model.Alarm
import com.example.skyradar.model.RepositoryImpl
import com.example.skyradar.network.RemoteDataSourceImpl
import com.example.skyradar.network.RetrofitInstance
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmFragment : Fragment(R.layout.fragment_alarm) {

    private lateinit var showTimePickerButton: Button
    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmRecyclerView: RecyclerView
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var viewModel: AlarmViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showTimePickerButton = view.findViewById(R.id.showTimePickerButton)
        alarmRecyclerView = view.findViewById(R.id.alarmRecyclerView)
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Initialize RecyclerView
        alarmAdapter = AlarmAdapter(emptyList())
        alarmRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        alarmRecyclerView.adapter = alarmAdapter

        val factory = AlarmViewModelFactory(
            RepositoryImpl(
                RemoteDataSourceImpl.getInstance(RetrofitInstance.retrofit),
                LocationLocalDataSourceImpl.getInstance(requireContext()),
                AlarmLocalDataSourceImpl.getInstance(requireContext())
            )
        )
        viewModel = ViewModelProvider(this, factory).get(AlarmViewModel::class.java)

        lifecycleScope.launch {
            viewModel.alarm.collect { alarms ->
                alarmAdapter.updateAlarms(alarms)
            }
        }


        showTimePickerButton.setOnClickListener {
            showTimePickerDialog()
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val alarmTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
                set(Calendar.SECOND, 0)
            }
            setAlarm(alarmTime.timeInMillis)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(timeInMillis: Long) {
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val requestCode = timeInMillis.toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Save the alarm using ViewModel
        val alarm = Alarm(timeInMillis = timeInMillis)
        viewModel.insertAlarm(alarm)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        Toast.makeText(requireContext(), "Alarm set!", Toast.LENGTH_SHORT).show()
    }
}
