package com.example.skyradar

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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
        settingsFloatingActionButton = findViewById(R.id.SettingsFloatingActionButton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onHomeButtonClicked()

        menuFloatingActionButton.setOnClickListener {
            onMenuButtonClicked()
        }

        homeFloatingActionButton.setOnClickListener {
            onHomeButtonClicked()
        }

        mapFloatingActionButton.setOnClickListener {
            onMapButtonClicked()
        }
        favFloatingActionButton.setOnClickListener {
            onFavButtonClicked()
        }
        settingsFloatingActionButton.setOnClickListener {
            onSettingsButtonClicked()
        }
    }


    private fun onMenuButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }


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



    private fun setVisibility(clicked: Boolean) {
        if(!clicked){
            homeFloatingActionButton.visibility = View.VISIBLE
            mapFloatingActionButton.visibility = View.VISIBLE
            favFloatingActionButton.visibility = View.VISIBLE
            settingsFloatingActionButton.visibility = View.VISIBLE
        } else {
            homeFloatingActionButton.visibility = View.INVISIBLE
            mapFloatingActionButton.visibility = View.INVISIBLE
            favFloatingActionButton.visibility = View.INVISIBLE
            settingsFloatingActionButton.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if(!clicked){
            menuFloatingActionButton.startAnimation(rotateOpenAnim)
            homeFloatingActionButton.startAnimation(fromBottomAnim)
            mapFloatingActionButton.startAnimation(fromBottomAnim)
            favFloatingActionButton.startAnimation(fromBottomAnim)
            settingsFloatingActionButton.startAnimation(fromBottomAnim)
        } else {
            menuFloatingActionButton.startAnimation(rotateCloseAnim)
            homeFloatingActionButton.startAnimation(toBottomAnim)
            mapFloatingActionButton.startAnimation(toBottomAnim)
            favFloatingActionButton.startAnimation(toBottomAnim)
            settingsFloatingActionButton.startAnimation(toBottomAnim)
        }
    }

    private fun setClickable(clicked: Boolean) {
        if(!clicked){
            homeFloatingActionButton.isClickable = true
            mapFloatingActionButton.isClickable = true
            favFloatingActionButton.isClickable = true
            settingsFloatingActionButton.isClickable = true
        } else {
            homeFloatingActionButton.isClickable = false
            mapFloatingActionButton.isClickable = false
            favFloatingActionButton.isClickable = false
            settingsFloatingActionButton.isClickable = false
        }
    }


}
