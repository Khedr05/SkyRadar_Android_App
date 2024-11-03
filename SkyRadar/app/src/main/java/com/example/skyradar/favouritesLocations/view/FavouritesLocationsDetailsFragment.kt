// FavouritesLocationsDetailsFragment.kt
package com.example.skyradar.favouritesLocations.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.skyradar.R
import com.example.skyradar.model.DatabasePojo

class FavouritesLocationsDetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_favourites_locations_details, container, false)

        // Retrieve the data
        val selectedLocation = arguments?.getSerializable("selected_location") as? DatabasePojo

        // Use selectedLocation to update UI or perform actions

        if (selectedLocation != null) {
            Log.i("FavouritesLocationsDetailsFragment", "Selected Location: ${selectedLocation.Weather.name}")
            Log.i("FavouritesLocationsDetailsFragment", "Selected Location: ${selectedLocation.Weather.main?.temp}")
        } else {
            Log.i("FavouritesLocationsDetailsFragment", "Selected Location is null")
        }


        return view
    }

}
