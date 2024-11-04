package com.example.skyradar.favouritesLocations.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.R
import com.example.skyradar.model.DatabasePojo

class FavouritesLocationsAdapter(
    private var locations: List<DatabasePojo>,
    private val onItemClick: (DatabasePojo) -> Unit // Callback for item clicks
) : RecyclerView.Adapter<FavouritesLocationsAdapter.LocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.bind(location, onItemClick)
    }

    override fun getItemCount(): Int = locations.size

    fun updateLocations(newLocations: List<DatabasePojo>) {
        locations = newLocations
        notifyDataSetChanged()
    }

    fun getLocationAtPosition(position: Int): DatabasePojo {
        return locations[position]
    }

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cityName: TextView = itemView.findViewById(R.id.tv_city_name)
        private val maxTemp: TextView = itemView.findViewById(R.id.tv_high_degree)
        private val minTemp: TextView = itemView.findViewById(R.id.tv_low_degree)
        private val currentWeather: TextView = itemView.findViewById(R.id.tv_weather_condition)
        private val weatherIcon: ImageView = itemView.findViewById(R.id.iv_icon_days)

        fun bind(location: DatabasePojo, onItemClick: (DatabasePojo) -> Unit) {
            // Safely accessing the Weather object
            val weather = location.Weather

            // Displaying the city name and weather info
            cityName.text = weather.name ?: "Unknown"
            currentWeather.text = weather.weather?.get(0)?.description
                ?.split(" ")
                ?.joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } } ?: "N/A"
            maxTemp.text = weather.main?.tempMax?.toString() ?: "N/A"
            minTemp.text = weather.main?.tempMin?.toString() ?: "N/A"

            // Set weather icon based on icon code
            val iconCode = weather.weather?.get(0)?.icon
            weatherIcon.setImageResource(getCustomIconForWeather(iconCode))

            // Set click listener for the item
            itemView.setOnClickListener {
                onItemClick(location) // Trigger the callback with the clicked location
            }
        }

        private fun getCustomIconForWeather(iconCode: String?): Int {
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
    }
}
