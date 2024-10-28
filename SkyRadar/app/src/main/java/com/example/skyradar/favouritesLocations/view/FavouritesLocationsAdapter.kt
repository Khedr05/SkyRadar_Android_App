package com.example.skyradar.favouritesLocations.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.R
import com.example.skyradar.model.DatabasePojo

class FavouritesLocationsAdapter(
    private var locations: List<DatabasePojo>
) : RecyclerView.Adapter<FavouritesLocationsAdapter.LocationViewHolder>() {

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityName: TextView = itemView.findViewById(R.id.text_city_name)
        val currentTemp: TextView = itemView.findViewById(R.id.text_current_temp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.cityName.text = location.Weather.name
        holder.currentTemp.text = "${location.Weather?.main?.temp ?: "N/A"}°" // Using safe calls and a default value
    }

    override fun getItemCount(): Int = locations.size

    // Method to update the list of locations
    fun updateLocations(newLocations: List<DatabasePojo>) {
        locations = newLocations
        notifyDataSetChanged()
    }
}
