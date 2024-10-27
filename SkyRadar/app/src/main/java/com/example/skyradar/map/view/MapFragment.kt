package com.example.skyradar.map.view

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.skyradar.R
import com.example.skyradar.WeatherDetailsFragment
import com.example.skyradar.map.viewmodel.MapFactory
import com.example.skyradar.map.viewmodel.MapViewModel
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import java.io.File
import java.util.Locale

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var searchBar: EditText
    private lateinit var geocoder: Geocoder
    private val mapViewModel: MapViewModel by lazy {
        MapFactory().create(MapViewModel::class.java)
    }

    private var selectedMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)

        // Initialize Geocoder
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        // Configure map settings
        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(Context.MODE_PRIVATE))
        Configuration.getInstance().osmdroidTileCache = File(context?.cacheDir, "osmdroid_tiles")

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(GeoPoint(37.7749, -122.4194))

        setupMapTapListener()
        //setupSearchListener()

        viewLifecycleOwner.lifecycleScope.launch {
            mapViewModel.selectedLocation.collect { location ->
                location?.let {
                    addMarkerAtLocation(it)
                    zoomToLocation(it)
                }
            }
        }

        return view
    }

    private fun setupMapTapListener() {
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let {
                    mapViewModel.updateSelectedLocation(it)
                    zoomToLocation(it)
                    Toast.makeText(context, "Selected Lat: ${it.latitude}, Lon: ${it.longitude}", Toast.LENGTH_SHORT).show()
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }

        val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
        mapView.overlays.add(mapEventsOverlay)
    }



    private fun addMarkerAtLocation(location: GeoPoint) {
        selectedMarker?.let {
            mapView.overlays.remove(it)
        }

        val marker = Marker(mapView)
        marker.position = location
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Selected Location"
        selectedMarker = marker
        mapView.overlays.add(marker)
        mapView.invalidate()
    }

    private fun zoomToLocation(location: GeoPoint) {
        mapView.controller.setCenter(location)
        mapView.controller.setZoom(19.5)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}