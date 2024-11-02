package com.example.skyradar.home.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.Helpers.formatTimestamp
import com.example.skyradar.Helpers.getHourFromUnixTime
import com.example.skyradar.R
import com.example.skyradar.model.WeatherList
import java.util.Calendar

class HourlyAdapter : ListAdapter<WeatherList, HourlyAdapter.HourlyWeatherViewHolder>(HourlyWeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_hourly, parent, false)
        return HourlyWeatherViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.scale_in_animation)
        holder.itemView.startAnimation(animation)
        holder.bind(getItem(position))
    }

    fun submitTodayWeather(list: List<WeatherList>) {
        // Filter the list to only include today's weather
        val todayList = list.filter { isToday(it.dt) }
        submitList(todayList)
    }

    inner class HourlyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDegreeDayHour: TextView = itemView.findViewById(R.id.tv_degree_day_hour)
        private val timeHour: TextView = itemView.findViewById(R.id.time_hour)
        private val imvWeatherHour: ImageView = itemView.findViewById(R.id.imv_weather_hour)

        fun bind(weather: WeatherList) {
            tvDegreeDayHour.text = weather.main.temp.toString()
            timeHour.text = formatTimestamp(weather.dt)
            val hour = getHourFromUnixTime(weather.dt)
            val icon = when (hour) {
                2, 5, 8, 11  -> R.drawable.ic_day_hour
                else -> R.drawable.ic_night_hour
            }
            imvWeatherHour.setImageResource(icon)
        }
    }

    class HourlyWeatherDiffCallback : DiffUtil.ItemCallback<WeatherList>() {
        override fun areItemsTheSame(oldItem: WeatherList, newItem: WeatherList): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: WeatherList, newItem: WeatherList): Boolean {
            return oldItem == newItem
        }
    }
}

// Helper function to check if a timestamp is from today
fun isToday(timestamp: Long): Boolean {
    val calendar = Calendar.getInstance()
    val today = Calendar.getInstance()

    calendar.timeInMillis = timestamp * 1000 // assuming timestamp is in seconds
    return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)
}
