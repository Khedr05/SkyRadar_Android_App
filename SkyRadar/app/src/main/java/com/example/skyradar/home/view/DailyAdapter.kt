package com.example.skyradar.home.view

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.R
import com.example.skyradar.model.WeatherList
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class DailyAdapter : ListAdapter<WeatherList, DailyAdapter.DailyWeatherViewHolder>(DailyWeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_daily, parent, false)
        return DailyWeatherViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_in_bottom)
        holder.bind(getItem(position))
        holder.itemView.startAnimation(animation)
    }

    inner class DailyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDayDays: TextView = itemView.findViewById(R.id.tv_day_days)
        private val tvWeatherCondition: TextView = itemView.findViewById(R.id.tv_weather_condition)
        private val tvHighDegree: TextView = itemView.findViewById(R.id.tv_high_degree)
        private val tvLowDegree: TextView = itemView.findViewById(R.id.tv_low_degree)
        private val ivIconDays: ImageView = itemView.findViewById(R.id.iv_icon_days)

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("DefaultLocale")
        fun bind(weather: WeatherList) {
            // Convert the timestamp to a LocalDate
            val date = Instant.ofEpochSecond(weather.dt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            // Determine if the date is today, tomorrow, or another day of the week
            val today = LocalDate.now()
            val dayString = when (date) {
                today -> itemView.context.getString(R.string.today)
                today.plusDays(1) -> itemView.context.getString(R.string.tomorrow)
                else -> date.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()))
            }

            // Display day, weather condition, and temperatures
            tvDayDays.text = dayString
            tvWeatherCondition.text = weather.weather[0].description
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
            tvHighDegree.text = weather.main.tempMax.toString()
            tvLowDegree.text = weather.main.tempMin.toString()

            // Set weather icon based on icon code
            val iconCode = weather.weather[0].icon
            ivIconDays.setImageResource(getCustomIconForWeather(iconCode))
        }
    }

    private fun getCustomIconForWeather(iconCode: String): Int {
        return when (iconCode) {
            "01d", "01n" -> R.drawable.ic_clear_sky
            "02d", "02n" -> R.drawable.ic_few_cloud
            "03d", "03n" -> R.drawable.ic_scattered_clouds
            "04d", "04n" -> R.drawable.ic_broken_clouds
            "09d", "09n" -> R.drawable.ic_shower_rain
            "10d", "10n" -> R.drawable.ic_rain
            "11d", "11n" -> R.drawable.ic_thunderstorm
            "13d", "13n" -> R.drawable.ic_snow
            "50d", "50n" -> R.drawable.ic_mist
            else -> R.drawable.ic_clear_sky
        }
    }

    fun submitWeatherList(weatherList: List<WeatherList>) {
        val filteredList = filterUniqueDays(weatherList)
        submitList(filteredList)
    }

    private fun filterUniqueDays(weatherList: List<WeatherList>): List<WeatherList> {
        val uniqueDaysMap = mutableMapOf<LocalDate, WeatherList>()

        for (weather in weatherList) {
            val date = Instant.ofEpochSecond(weather.dt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            // Only add the first weather entry for each day
            if (!uniqueDaysMap.containsKey(date)) {
                uniqueDaysMap[date] = weather
            }
        }

        return uniqueDaysMap.values.toList()
    }

    class DailyWeatherDiffCallback : DiffUtil.ItemCallback<WeatherList>() {
        override fun areItemsTheSame(oldItem: WeatherList, newItem: WeatherList): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: WeatherList, newItem: WeatherList): Boolean {
            return oldItem == newItem
        }
    }
}
