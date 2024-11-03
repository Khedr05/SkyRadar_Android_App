package com.example.skyradar.favouritesLocations.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skyradar.R
import com.example.skyradar.database.AlarmLocalDataSourceImpl
import com.example.skyradar.database.LocationLocalDataSourceImpl
import com.example.skyradar.favouritesLocations.viewmodel.FavouritesLocationsViewModel
import com.example.skyradar.favouritesLocations.viewmodel.FavouritesLocationsViewModelFactory
import com.example.skyradar.model.DatabasePojo
import com.example.skyradar.model.RepositoryImpl
import com.example.skyradar.network.RemoteDataSourceImpl
import com.example.skyradar.network.RetrofitInstance
import kotlinx.coroutines.launch

class FavouritesLocationsFragment : Fragment() {

    private lateinit var viewModel: FavouritesLocationsViewModel
    private lateinit var adapter: FavouritesLocationsAdapter
    private lateinit var recyclerViewFavoriteLocations: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = FavouritesLocationsViewModelFactory(
            RepositoryImpl(
                RemoteDataSourceImpl.getInstance(RetrofitInstance.retrofit),
                LocationLocalDataSourceImpl.getInstance(requireContext()),
                AlarmLocalDataSourceImpl.getInstance(requireContext())
            )
        )
        viewModel = ViewModelProvider(this, factory).get(FavouritesLocationsViewModel::class.java)
        return inflater.inflate(R.layout.fragment_favourites_locations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewFavoriteLocations = view.findViewById(R.id.recyclerViewFavoriteLocations)

        adapter = FavouritesLocationsAdapter(emptyList()) { location ->
            // Navigate to details fragment
            val detailsFragment = FavouritesLocationsDetailsFragment()
            val args = Bundle().apply {
                putSerializable("selected_location", location) // Pass the location data
            }
            detailsFragment.arguments = args

            // Use FragmentTransaction to navigate
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    detailsFragment
                ) // Make sure to replace with your container ID
                .addToBackStack(null)
                .commit()
        }

        recyclerViewFavoriteLocations.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewFavoriteLocations.adapter = adapter

        lifecycleScope.launch {
            viewModel.favoriteLocations.collect { locations ->
                adapter.updateLocations(locations)
            }
        }
    }
}