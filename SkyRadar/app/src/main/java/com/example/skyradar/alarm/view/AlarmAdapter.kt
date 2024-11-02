package com.example.skyradar.alarm.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.R
import java.util.Calendar

class AlarmAdapter(
    private val context: Context,
    private var alarms: List<Alarm>,
    private val onToggleListener: (Alarm, Boolean) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    inner class AlarmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAlarmTime: TextView = view.findViewById(R.id.tvAlarmTime)
        val switchAlarm: Switch = view.findViewById(R.id.switchAlarm)

        fun bind(alarm: Alarm) {
            val calendar = Calendar.getInstance().apply { timeInMillis = alarm.timeInMillis }
            val hour = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)
            val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
            tvAlarmTime.text = String.format("%02d:%02d %s", hour, minute, amPm)

            switchAlarm.isChecked = alarm.isActive

            switchAlarm.setOnCheckedChangeListener { _, isChecked ->
                onToggleListener(alarm, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.alarm_item, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(alarms[position])
    }

    override fun getItemCount() = alarms.size

    fun updateAlarms(newAlarms: List<Alarm>) {
        alarms = newAlarms
        notifyDataSetChanged()
    }
}
