package com.example.skyradar

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import com.example.skyradar.alarm.view.AlarmFragment
import com.example.skyradar.favouritesLocations.view.FavouritesLocationsFragment
import com.example.skyradar.map.view.MapFragment
import com.example.skyradar.home.view.HomeFragment
import com.example.skyradar.settings.view.SettingsFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RootActivity : AppCompatActivity() {

    private val rotateOpenAnim: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateCloseAnim: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottomAnim: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottomAnim: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }
    private lateinit var menuFloatingActionButton: FloatingActionButton
    private lateinit var homeFloatingActionButton: FloatingActionButton
    private lateinit var mapFloatingActionButton: FloatingActionButton
    private lateinit var favFloatingActionButton: FloatingActionButton
    private lateinit var alarmFloatingActionButton: FloatingActionButton
    private lateinit var settingsFloatingActionButton: FloatingActionButton
    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_root)

        menuFloatingActionButton = findViewById(R.id.menuFloatingActionButton)
        homeFloatingActionButton = findViewById(R.id.HomeFloatingActionButton)
        mapFloatingActionButton = findViewById(R.id.MapFloatingActionButton)
        favFloatingActionButton = findViewById(R.id.FavFloatingActionButton)
        alarmFloatingActionButton = findViewById(R.id.AlarmFloatingActionButton)
        settingsFloatingActionButton = findViewById(R.id.SettingsFloatingActionButton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set the default fragment
        onHomeButtonClicked()

        // Set up the click listeners
        menuFloatingActionButton.setOnClickListener {
            onMenuButtonClicked()
        }

        homeFloatingActionButton.setOnClickListener {
            onFragmentButtonClicked { onHomeButtonClicked() }
        }

        mapFloatingActionButton.setOnClickListener {
            onFragmentButtonClicked { onMapButtonClicked() }
        }

        favFloatingActionButton.setOnClickListener {
            onFragmentButtonClicked { onFavButtonClicked() }
        }

        settingsFloatingActionButton.setOnClickListener {
            onFragmentButtonClicked { onSettingsButtonClicked() }
        }

        alarmFloatingActionButton.setOnClickListener {
            onFragmentButtonClicked { onAlarmButtonClicked() }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm Channel"
            val descriptionText = "Channel for Alarm notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("alarm_channel", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        // Check if the current fragment is a DialogFragment
        if (fragment is DialogFragment) {
            fragment.dismiss() // Dismiss the dialog
            clicked = false // Close the navigation slider
            setVisibility(false)
            setAnimation(true)
            setClickable(false)
        } else {
            super.onBackPressed() // Default behavior
        }
    }

    private fun onFragmentButtonClicked(action: () -> Unit) {
        // Close the navigation slider if it is open
        if (clicked) {
            clicked = false
            setVisibility(false)
            setAnimation(true)
            setClickable(false)
        }
        action() // Perform the fragment change
    }
//    // Override back press behavior
//    override fun onBackPressed() {
//        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
//
//        // Check if the current fragment is a DialogFragment
//        if (fragment is DialogFragment) {
//            fragment.dismiss() // Dismiss the dialog
//        } else {
//            super.onBackPressed() // Default behavior
//        }
//    }

    private fun onMenuButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

//    private fun onFragmentButtonClicked(action: () -> Unit) {
//        // Close the navigation slider
//        if (clicked) {
//            clicked = false
//            setVisibility(false)
//            setAnimation(true)
//            setClickable(false)
//        }
//        action()
//    }

    private fun onHomeButtonClicked() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun onMapButtonClicked() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, MapFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun onFavButtonClicked() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, FavouritesLocationsFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun onSettingsButtonClicked() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, SettingsFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun onAlarmButtonClicked() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, AlarmFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            homeFloatingActionButton.visibility = View.VISIBLE
            mapFloatingActionButton.visibility = View.VISIBLE
            favFloatingActionButton.visibility = View.VISIBLE
            alarmFloatingActionButton.visibility = View.VISIBLE
            settingsFloatingActionButton.visibility = View.VISIBLE
        } else {
            homeFloatingActionButton.visibility = View.INVISIBLE
            mapFloatingActionButton.visibility = View.INVISIBLE
            favFloatingActionButton.visibility = View.INVISIBLE
            alarmFloatingActionButton.visibility = View.INVISIBLE
            settingsFloatingActionButton.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            menuFloatingActionButton.startAnimation(rotateOpenAnim)
            homeFloatingActionButton.startAnimation(fromBottomAnim)
            mapFloatingActionButton.startAnimation(fromBottomAnim)
            favFloatingActionButton.startAnimation(fromBottomAnim)
            alarmFloatingActionButton.startAnimation(fromBottomAnim)
            settingsFloatingActionButton.startAnimation(fromBottomAnim)
        } else {
            menuFloatingActionButton.startAnimation(rotateCloseAnim)
            homeFloatingActionButton.startAnimation(toBottomAnim)
            mapFloatingActionButton.startAnimation(toBottomAnim)
            favFloatingActionButton.startAnimation(toBottomAnim)
            alarmFloatingActionButton.startAnimation(toBottomAnim)
            settingsFloatingActionButton.startAnimation(toBottomAnim)
        }
    }

    private fun setClickable(clicked: Boolean) {
        if (!clicked) {
            homeFloatingActionButton.isClickable = true
            mapFloatingActionButton.isClickable = true
            favFloatingActionButton.isClickable = true
            alarmFloatingActionButton.isClickable = true
            settingsFloatingActionButton.isClickable = true
        } else {
            homeFloatingActionButton.isClickable = false
            mapFloatingActionButton.isClickable = false
            favFloatingActionButton.isClickable = false
            alarmFloatingActionButton.isClickable = false
            settingsFloatingActionButton.isClickable = false
        }
    }
}
