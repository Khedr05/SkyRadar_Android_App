package com.example.skyradar.home.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.R
import com.example.skyradar.model.WeatherList

class HomeAdapter : ListAdapter<WeatherList, HomeAdapter.WeatherViewHolder>(WeatherDiffCallback()) {

    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cityNameView: TextView = itemView.findViewById(R.id.cityNameView)
        private val temperatureView: TextView = itemView.findViewById(R.id.temperatureView)

        fun bind(weather: WeatherList) {
            cityNameView.text = weather.dtTxt
            temperatureView.text = "${weather.main.temp} °C"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class WeatherDiffCallback : DiffUtil.ItemCallback<WeatherList>() {
        override fun areItemsTheSame(oldItem: WeatherList, newItem: WeatherList): Boolean {
            return oldItem.weather == newItem.weather
        }

        override fun areContentsTheSame(oldItem: WeatherList, newItem: WeatherList): Boolean {
            return oldItem == newItem
        }
    }
}
