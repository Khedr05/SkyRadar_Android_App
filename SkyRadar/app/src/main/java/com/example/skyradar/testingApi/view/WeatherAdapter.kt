package com.example.skyradar.testingApi.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.R
import com.example.skyradar.model.WeatherList // Adjust the import to match your actual model

class WeatherAdapter : ListAdapter<WeatherList, WeatherAdapter.WeatherViewHolder>(WeatherDiffCallback()) {

    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cityNameView: TextView = itemView.findViewById(R.id.cityNameView)
        private val temperatureView: TextView = itemView.findViewById(R.id.temperatureView)

        // Bind WeatherList data
        fun bind(weather: WeatherList) {
            cityNameView.text = weather.dtTxt // Adjust this based on your WeatherList model structure
            temperatureView.text = "${weather.main.temp} °C" // Display temperature
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
            // Assuming that city ID is unique, modify this to match your model's unique identifier
            return oldItem.weather == newItem.weather
        }

        override fun areContentsTheSame(oldItem: WeatherList, newItem: WeatherList): Boolean {
            return oldItem == newItem // Assuming the data class properly overrides equals()
        }
    }
}
