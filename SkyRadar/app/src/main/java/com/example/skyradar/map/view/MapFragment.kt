package com.example.skyradar.map.view

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.skyradar.R
import com.example.skyradar.map.viewmodel.MapFactory
import com.example.skyradar.map.viewmodel.MapViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.events.MapEventsReceiver
import java.io.File
import java.util.Locale
import android.view.inputmethod.InputMethodManager


class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private var currentLocationMarker: Marker? = null
    private val mapViewModel: MapViewModel by lazy {
        MapFactory().create(MapViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = requireContext()
        val config = Configuration.getInstance()

        config.load(context, androidx.preference.PreferenceManager.getDefaultSharedPreferences(context))
        config.osmdroidBasePath = File(context.cacheDir, "osmdroid")
        config.osmdroidTileCache = File(context.cacheDir, "tiles")
        config.cacheMapTileCount = 12
        config.tileDownloadThreads = 4
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)
        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView)

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.setBuiltInZoomControls(true)

        val gizaPyramidsGeoPoint = GeoPoint(29.9792, 31.1342)
        mapView.controller.setZoom(17.0)
        mapView.controller.setCenter(gizaPyramidsGeoPoint)

        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                addMarkerAtLocation(p)
                showWeatherDetailsFragment(p.latitude, p.longitude) // Navigate to WeatherDetailsFragment
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }
        val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
        mapView.overlays.add(mapEventsOverlay)

        // Setup auto-complete functionality
        setupAutoComplete()

        return view
    }

    private fun setupAutoComplete() {
        val geocoder = Geocoder(requireContext(), Locale("ar")) // Use Arabic locale

        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedLocation = parent.getItemAtPosition(position).toString()
            searchLocation(selectedLocation)

            // Hide the keyboard after selecting an item
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(autoCompleteTextView.windowToken, 0)
        }

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    // Get location suggestions in Arabic
                    val arabicResults: List<Address>? = geocoder.getFromLocationName(query, 5)
                    val arabicLocationNames = arabicResults?.map { it.getAddressLine(0) } ?: emptyList()

                    // Now get location suggestions in English
                    val englishGeocoder = Geocoder(requireContext(), Locale("en"))
                    val englishResults: List<Address>? = englishGeocoder.getFromLocationName(query, 5)
                    val englishLocationNames = englishResults?.map { it.getAddressLine(0) } ?: emptyList()

                    // Combine the results
                    val combinedLocationNames = (arabicLocationNames + englishLocationNames).distinct()

                    // Update the adapter with the filtered results
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, combinedLocationNames)
                    autoCompleteTextView.setAdapter(adapter)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }



    private fun searchLocation(locationName: String) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        val results: List<Address>? = geocoder.getFromLocationName(locationName, 1)

        if (!results.isNullOrEmpty()) {
            val geoPoint = GeoPoint(results[0].latitude, results[0].longitude)
            addMarkerAtLocation(geoPoint) // Add marker at the found location
            mapView.controller.animateTo(geoPoint, 17.0, 1000L) // Move the map to the found location
        } else {
            Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    private fun addMarkerAtLocation(geoPoint: GeoPoint) {
        mapView.overlays.remove(currentLocationMarker)

        currentLocationMarker = Marker(mapView)
        currentLocationMarker?.apply {
            position = geoPoint
            title = "Tapped Location"
            mapView.overlays.add(this)
        }

        Toast.makeText(requireContext(), "Marker placed at: ${geoPoint.latitude}, ${geoPoint.longitude}", Toast.LENGTH_SHORT).show()

        mapView.controller.animateTo(geoPoint, 18.0, 1000L)
        mapView.invalidate()
    }

    private fun showWeatherDetailsFragment(latitude: Double, longitude: Double) {
        val weatherFragment = MapWeatherDetailsFragment()
        val bundle = Bundle().apply {
            putDouble("LATITUDE", latitude)
            putDouble("LONGITUDE", longitude)
        }
        weatherFragment.arguments = bundle

        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, weatherFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
