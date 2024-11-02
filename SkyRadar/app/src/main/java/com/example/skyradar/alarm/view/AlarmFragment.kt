package com.example.skyradar.alarm.view

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.R
import com.example.skyradar.alarm.viewmodel.AlarmViewModel
import com.example.skyradar.alarm.viewmodel.AlarmViewModelFactory
import com.example.skyradar.database.AlarmLocalDataSourceImpl
import com.example.skyradar.database.LocationLocalDataSourceImpl
import com.example.skyradar.model.Alarm
import com.example.skyradar.model.RepositoryImpl
import com.example.skyradar.network.RemoteDataSourceImpl
import com.example.skyradar.network.RetrofitInstance
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmFragment : Fragment(R.layout.fragment_alarm) {

    private lateinit var showTimePickerButton: ImageButton
    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmRecyclerView: RecyclerView
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var viewModel: AlarmViewModel

    private val backgroundColor = ColorDrawable(Color.RED)
    private lateinit var deleteIcon: Drawable
    private val iconMargin = 32 // margin between the icon and the edge of the item view

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI components
        showTimePickerButton = view.findViewById(R.id.showTimePickerButton)
        alarmRecyclerView = view.findViewById(R.id.alarmRecyclerView)
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.delete)!!

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

        // Setup ItemTouchHelper for swipe-to-delete with icon and background
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val alarm = alarmAdapter.getAlarmAtPosition(position)

                // Cancel the alarm
                cancelAlarm(alarm)

                // Delete the alarm from ViewModel
                viewModel.deleteAlarm(alarm)
                Toast.makeText(requireContext(), "Alarm deleted", Toast.LENGTH_SHORT).show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
                    val iconBottom = iconTop + deleteIcon.intrinsicHeight

                    if (dX > 0) { // Swiping to the right
                        val iconLeft = itemView.left + iconMargin
                        val iconRight = iconLeft + deleteIcon.intrinsicWidth
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        backgroundColor.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                    } else if (dX < 0) { // Swiping to the left
                        val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                        val iconRight = itemView.right - iconMargin
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        backgroundColor.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    } else {
                        backgroundColor.setBounds(0, 0, 0, 0)
                    }

                    backgroundColor.draw(c)
                    deleteIcon.draw(c)
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(alarmRecyclerView)
    }

    private fun cancelAlarm(alarm: Alarm) {
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alarm.timeInMillis.toInt(),  // Use the unique ID you used to set the alarm
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent) // Cancel the alarm
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            // Adjust for 12-hour format and AM/PM
            val isPM = selectedHour >= 12
            val adjustedHour = if (selectedHour % 12 == 0) 12 else selectedHour % 12  // Ensure hour is 1-12

            val alarmTime = Calendar.getInstance().apply {
                set(Calendar.HOUR, adjustedHour)
                set(Calendar.MINUTE, selectedMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.AM_PM, if (isPM) Calendar.PM else Calendar.AM)
            }
            setAlarm(alarmTime.timeInMillis)
        }, hour, minute, false)  // Set to 12-hour format

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
