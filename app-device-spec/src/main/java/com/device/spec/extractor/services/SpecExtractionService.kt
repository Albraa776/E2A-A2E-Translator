package com.device.spec.extractor.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.device.spec.extractor.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Foreground service for continuous sensor monitoring and device spec extraction
 */
@AndroidEntryPoint
class SpecExtractionService : Service(), SensorEventListener {
    
    @Inject
    lateinit var sensorManager: SensorManager
    
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    
    private var isCollecting = false
    private var sensorData = mutableMapOf<String, Any>()
    
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification())
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startCollection()
            ACTION_STOP -> stopCollection()
            else -> stopSelf()
        }
        return START_STICKY
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopCollection()
        job.cancel()
    }
    
    /**
     * Start sensor data collection
     */
    private fun startCollection() {
        if (isCollecting) return
        
        isCollecting = true
        sensorData.clear()
        
        // Register sensor listeners
        registerSensorListeners()
        
        // Start collection coroutine
        scope.launch {
            collectSensorData()
        }
    }
    
    /**
     * Stop sensor data collection
     */
    private fun stopCollection() {
        isCollecting = false
        
        // Unregister sensor listeners
        sensorManager.unregisterListener(this)
        
        // Stop the service
        stopForeground(true)
        stopSelf()
    }
    
    /**
     * Register sensor listeners
     */
    private fun registerSensorListeners() {
        val sensors = listOf(
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_PRESSURE,
            Sensor.TYPE_LIGHT,
            Sensor.TYPE_PROXIMITY,
            Sensor.TYPE_STEP_COUNTER,
            Sensor.TYPE_STEP_DETECTOR
        )
        
        sensors.forEach { sensorType ->
            val sensor = sensorManager.getDefaultSensor(sensorType)
            if (sensor != null) {
                sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        }
    }
    
    /**
     * Collect sensor data in background
     */
    private suspend fun collectSensorData() {
        while (isCollecting) {
            // Collect additional sensor data
            delay(1000) // Collect every second
            
            // Update notification
            updateNotification()
        }
    }
    
    /**
     * Create notification channel
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Device Spec Extraction",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
    
    /**
     * Create notification
     */
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Device Spec Extractor")
            .setContentText("Collecting device specifications...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }
    
    /**
     * Update notification
     */
    private fun updateNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Device Spec Extractor")
            .setContentText("Collecting device specifications... ${sensorData.size} sensors")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(1, notification)
    }
    
    /**
     * Sensor event callbacks
     */
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            val sensorName = sensorEvent.sensor.name
            val sensorType = sensorEvent.sensor.type
            
            // Store sensor data
            sensorData[sensorName] = mapOf(
                "type" to sensorType,
                "timestamp" to System.currentTimeMillis(),
                "values" to sensorEvent.values.toList(),
                "accuracy" to sensorEvent.accuracy
            )
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }
    
    companion object {
        const val CHANNEL_ID = "device_spec_extraction_channel"
        const val ACTION_START = "com.device.spec.extractor.START"
        const val ACTION_STOP = "com.device.spec.extractor.STOP"
        
        fun startService(context: Context) {
            val intent = Intent(context, SpecExtractionService::class.java).apply {
                action = ACTION_START
            }
            context.startService(intent)
        }
        
        fun stopService(context: Context) {
            val intent = Intent(context, SpecExtractionService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}