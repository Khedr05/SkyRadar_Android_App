package com.example.skyradar.favouritesLocations.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
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
    private val backgroundColor = ColorDrawable(Color.RED)
    private lateinit var deleteIcon: Drawable
    private val iconMargin = 32 // margin between the icon and the edge of the item view

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = FavouritesLocationsViewModelFactory(
            RepositoryImpl(
                RemoteDataSourceImpl.getInstance(RetrofitInstance.retrofit),
                LocationLocalDataSourceImpl.getInstance(requireContext()),
                AlarmLocalDataSourceImpl.getInstance(requireContext()),
                requireContext().getSharedPreferences("settings_prefs", Context.MODE_PRIVATE))
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

        // Initialize the delete icon
        deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.delete)!!

        // Setup ItemTouchHelper for swipe-to-delete
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val location = adapter.getLocationAtPosition(position)

                // Delete the location from ViewModel
                viewModel.removeFavoriteLocation(location)
                Toast.makeText(requireContext(), "Location deleted", Toast.LENGTH_SHORT).show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
                    val iconBottom = iconTop + deleteIcon.intrinsicHeight

                    if (dX > 0) { // Swiping to the right
                        val iconLeft = itemView.left + iconMargin
                        val iconRight = iconLeft + deleteIcon.intrinsicWidth
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        backgroundColor.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                    } else if (dX < 0) { // Swiping to the left
                        val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                        val iconRight = itemView.right - iconMargin
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        backgroundColor.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    } else {
                        backgroundColor.setBounds(0, 0, 0, 0)
                    }

                    backgroundColor.draw(c)
                    deleteIcon.draw(c)
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerViewFavoriteLocations)
    }
}
