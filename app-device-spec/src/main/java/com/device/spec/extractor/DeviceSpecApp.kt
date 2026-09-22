package com.device.spec.extractor

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Main application class for Device Spec Extractor
 */
@HiltAndroidApp
class DeviceSpecApp : Application() {
    
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Initialize other components
        initializeComponents()
    }
    
    private fun initializeComponents() {
        // Initialize preferences
        // Initialize database
        // Initialize other app components
    }
}