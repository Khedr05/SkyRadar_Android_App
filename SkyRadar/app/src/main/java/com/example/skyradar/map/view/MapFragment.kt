package com.example.skyradar.map.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.skyradar.R
import com.example.skyradar.map.viewmodel.MapFactory
import com.example.skyradar.map.viewmodel.MapViewModel
import com.google.android.gms.location.*
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import android.app.AlertDialog
import android.provider.Settings
import android.widget.Toast
import org.osmdroid.api.IGeoPoint
import java.io.File

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isAlertDialogShown = false
    private var currentLocationMarker: Marker? = null // Store the current location marker

    private val mapViewModel: MapViewModel by lazy {
        MapFactory().create(MapViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = requireContext()
        Configuration.getInstance().load(context, androidx.preference.PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().osmdroidBasePath = File(context.cacheDir, "osmdroid")
        Configuration.getInstance().osmdroidTileCache = File(context.cacheDir, "tiles")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)

        mapView.setTileSource(TileSourceFactory.MAPNIK) // Change to a tile source that includes POIs
        mapView.setMultiTouchControls(true)
        mapView.setBuiltInZoomControls(true)

        checkLocationEnabled()

        // Add a tap listener on the map to add a marker at the tapped location
        mapView.setOnClickListener { event ->
            val geoPoint = mapView.projection.fromPixels(event.x.toInt(), event.y.toInt())
            addMarkerAtLocation(geoPoint) // Pass geoPoint directly as IGeoPoint
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        checkLocationEnabled()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        // Stop location updates to conserve battery
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun checkLocationEnabled() {
        val locationManager = requireContext().getSystemService(LocationManager::class.java)
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            if (!isAlertDialogShown) {
                showLocationEnableAlert()
                isAlertDialogShown = true
            }
        } else {
            isAlertDialogShown = false
            checkLocationPermissionAndRequestLocation()
        }
    }

    private fun showLocationEnableAlert() {
        AlertDialog.Builder(requireContext())
            .setTitle("Enable Location Services")
            .setMessage("This app requires location services to provide your current location. Please enable location services in your settings.")
            .setPositiveButton("Enable") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                isAlertDialogShown = false
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                isAlertDialogShown = false
            }
            .show()
    }

    private fun checkLocationPermissionAndRequestLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestCurrentLocation()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun requestCurrentLocation() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000 // Update every 10 seconds
            fastestInterval = 5000 // Fastest update every 5 seconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // Handle multiple location results
            for (location in locationResult.locations) {
                showCurrentLocationOnMap(location)
            }
        }
    }

    private fun showCurrentLocationOnMap(location: android.location.Location) {
        val geoPoint = GeoPoint(location.latitude, location.longitude)

        // Center and zoom in on the map
        mapView.controller.setZoom(17.0)
        mapView.controller.setCenter(geoPoint)

        // Clear previous markers if needed
        mapView.overlays.clear() // This will clear all overlays including previous markers

        // Add a new marker for the current location
        currentLocationMarker = Marker(mapView)
        currentLocationMarker?.position = geoPoint
        currentLocationMarker?.title = "Current Location"

        mapView.overlays.add(currentLocationMarker)

        // Refresh the map
        mapView.invalidate()
    }

    private fun addMarkerAtLocation(geoPoint: IGeoPoint) {
        // Clear previous markers
        mapView.overlays.clear()

        // Create a new GeoPoint from the IGeoPoint
        val newGeoPoint = GeoPoint(geoPoint.latitude, geoPoint.longitude)

        // Create a new marker at the tapped location
        val newMarker = Marker(mapView)
        newMarker.position = newGeoPoint
        newMarker.title = "Tapped Location"

        // Add the new marker to the map
        mapView.overlays.add(newMarker)

        // Show the coordinates of the tapped location
        Toast.makeText(requireContext(), "Marker placed at: ${newGeoPoint.latitude}, ${newGeoPoint.longitude}", Toast.LENGTH_SHORT).show()

        // Refresh the map
        mapView.invalidate()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission is required to access your current location.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
