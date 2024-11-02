package com.example.skyradar.alarm.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.R
import com.example.skyradar.model.Alarm
import java.text.SimpleDateFormat
import java.util.*

class AlarmAdapter(private var alarms: List<Alarm>) :
    RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val alarmTimeTextView: TextView = itemView.findViewById(R.id.alarmTimeTextView)
        val alarmDateTextView: TextView = itemView.findViewById(R.id.alarmDateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_item, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())  // 12-hour format with AM/PM
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedTime = timeFormat.format(Date(alarm.timeInMillis))
        val formattedDate = dateFormat.format(Date(alarm.timeInMillis))
        holder.alarmTimeTextView.text = formattedTime
        holder.alarmDateTextView.text = formattedDate
    }

    override fun getItemCount() = alarms.size

    fun updateAlarms(newAlarms: List<Alarm>) {
        alarms = newAlarms
        notifyDataSetChanged()
    }

    fun getAlarmAtPosition(position: Int): Alarm {
        return alarms[position]
    }
}
