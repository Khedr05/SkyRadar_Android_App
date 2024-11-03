package com.example.skyradar.favouritesLocations.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.R
import com.example.skyradar.model.DatabasePojo

class FavouritesLocationsAdapter(
    private var locations: List<DatabasePojo>,
    private val onItemClick: (DatabasePojo) -> Unit // Callback for item clicks
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
        holder.currentTemp.text = "${location.Weather.main?.temp ?: "N/A"}°" // Using safe call and default value

        // Set click listener for the item
        holder.itemView.setOnClickListener {
            onItemClick(location) // Trigger the callback with the clicked location
        }
    }

    override fun getItemCount(): Int = locations.size

    fun updateLocations(newLocations: List<DatabasePojo>) {
        locations = newLocations
        notifyDataSetChanged()
    }
}
