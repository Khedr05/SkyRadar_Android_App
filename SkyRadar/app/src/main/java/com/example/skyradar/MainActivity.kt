package com.example.skyradar

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Start WeatherActivity directly without any button
        val intent = Intent(this, RootActivity::class.java)
        startActivity(intent)

        // Optional: Finish MainActivity so the user cannot return to it
        finish() // This line will close MainActivity after starting WeatherActivity
    }
}
