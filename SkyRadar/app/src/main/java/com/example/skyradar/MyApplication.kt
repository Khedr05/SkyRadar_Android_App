package com.example.skyradar

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null
        val context: Context
            get() = instance!!.applicationContext
    }
}
