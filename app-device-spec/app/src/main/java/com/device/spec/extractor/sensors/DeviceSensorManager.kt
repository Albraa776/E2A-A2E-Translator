package com.device.spec.extractor.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.pow

/**
 * Sensor manager for collecting and validating sensor data
 */
class DeviceSensorManager(
    private val context: Context
) : SensorEventListener {
    
    private val androidSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    private val _sensorData = MutableStateFlow<Map<String, SensorReading>>(emptyMap())
    val sensorData: StateFlow<Map<String, SensorReading>> = _sensorData
    
    private val _sensorAvailability = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val sensorAvailability: StateFlow<Map<Int, Boolean>> = _sensorAvailability
    
    private val _isCollecting = MutableStateFlow(false)
    val isCollecting: StateFlow<Boolean> = _isCollecting
    
    private var sensorReadings = mutableMapOf<String, SensorReading>()
    private var availableSensors = mutableMapOf<Int, Boolean>()
    
    /**
     * Start collecting sensor data
     */
    fun startCollection() {
        if (_isCollecting.value) return
        
        _isCollecting.value = true
        sensorReadings.clear()
        availableSensors.clear()
        
        // Register for all available sensors
        registerSensors()
    }
    
    /**
     * Stop collecting sensor data
     */
    fun stopCollection() {
        if (!_isCollecting.value) return
        
        _isCollecting.value = false
        
        // Unregister all sensors
        androidSensorManager.unregisterListener(this)
        
        // Update state
        updateSensorState()
    }
    
    /**
     * Register sensors for collection
     */
    private fun registerSensors() {
        val sensorTypes = listOf(
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_PRESSURE,
            Sensor.TYPE_LIGHT,
            Sensor.TYPE_PROXIMITY,
            Sensor.TYPE_STEP_COUNTER,
            Sensor.TYPE_STEP_DETECTOR,
            Sensor.TYPE_AMBIENT_TEMPERATURE,
            Sensor.TYPE_RELATIVE_HUMIDITY,
            Sensor.TYPE_TEMPERATURE
        )
        
        sensorTypes.forEach { sensorType ->
            val sensor = androidSensorManager.getDefaultSensor(sensorType)
            if (sensor != null) {
                availableSensors[sensorType] = true
                androidSensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            } else {
                availableSensors[sensorType] = false
            }
        }
        
        _sensorAvailability.value = availableSensors
    }
    
    /**
     * Update sensor state
     */
    private fun updateSensorState() {
        _sensorData.value = sensorReadings.toMap()
    }
    
    /**
     * Get sensor availability
     */
    fun getSensorAvailability(): Map<Int, Boolean> {
        return availableSensors.toMap()
    }
    
    /**
     * Get sensor reading by name
     */
    fun getSensorReading(sensorName: String): SensorReading? {
        return sensorReadings[sensorName]
    }
    
    /**
     * Get all sensor readings
     */
    fun getAllSensorReadings(): Map<String, SensorReading> {
        return sensorReadings.toMap()
    }
    
    /**
     * Validate sensor data integrity
     */
    fun validateSensorData(): SensorValidationResult {
        val issues = mutableListOf<String>()
        var isValid = true
        
        // Check if we have data from critical sensors
        val criticalSensors = listOf(
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_MAGNETIC_FIELD
        )
        
        criticalSensors.forEach { sensorType ->
            val sensor = androidSensorManager.getDefaultSensor(sensorType)
            if (sensor != null) {
                val sensorName = sensor.name
                val reading = sensorReadings[sensorName]
                if (reading == null) {
                    issues.add("No data from $sensorName")
                    isValid = false
                } else {
                    // Check if reading is reasonable
                    if (reading.values.isEmpty()) {
                        issues.add("Empty values from $sensorName")
                        isValid = false
                    } else {
                        // Validate accelerometer data (should have gravity component)
                        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                            val magnitude = Math.sqrt(
                                reading.values[0].toDouble().pow(2) +
                                reading.values[1].toDouble().pow(2) +
                                reading.values[2].toDouble().pow(2)
                            )
                            
                            if (magnitude < 5.0 || magnitude > 15.0) {
                                issues.add("Unreasonable accelerometer magnitude: $magnitude")
                                isValid = false
                            }
                        }
                        
                        // Validate gyroscope data (should be small when stationary)
                        if (sensorType == Sensor.TYPE_GYROSCOPE) {
                            val magnitude = Math.sqrt(
                                reading.values[0].toDouble().pow(2) +
                                reading.values[1].toDouble().pow(2) +
                                reading.values[2].toDouble().pow(2)
                            )
                            
                            if (magnitude > 1.0) {
                                issues.add("High gyroscope magnitude: $magnitude")
                                isValid = false
                            }
                        }
                    }
                }
            }
        }
        
        return SensorValidationResult(isValid, issues)
    }
    
    /**
     * Detect potential sensor spoofing
     */
    fun detectSensorSpoofing(): SpoofingDetectionResult {
        val spoofingIndicators = mutableListOf<String>()
        var isSpoofingDetected = false
        
        // Check for inconsistent sensor data
        val accelerometer = sensorReadings.values.find { 
            it.sensorType == Sensor.TYPE_ACCELEROMETER 
        }
        val gyroscope = sensorReadings.values.find { 
            it.sensorType == Sensor.TYPE_GYROSCOPE 
        }
        val magnetometer = sensorReadings.values.find { 
            it.sensorType == Sensor.TYPE_MAGNETIC_FIELD 
        }
        
        // Check for unrealistic sensor combinations
        if (accelerometer != null && gyroscope != null) {
            // Check if accelerometer and gyroscope are perfectly correlated
            // (which might indicate spoofing)
            if (areSensorsCorrelated(accelerometer, gyroscope, 0.95)) {
                spoofingIndicators.add("High correlation between accelerometer and gyroscope")
                isSpoofingDetected = true
            }
        }
        
        // Check for magnetic field anomalies
        if (magnetometer != null) {
            val magnitude = Math.sqrt(
                magnetometer.values[0].toDouble().pow(2) +
                magnetometer.values[1].toDouble().pow(2) +
                magnetometer.values[2].toDouble().pow(2)
            )
            
            // Earth's magnetic field is typically 25-65 µT
            if (magnitude < 10.0 || magnitude > 100.0) {
                spoofingIndicators.add("Unreasonable magnetic field magnitude: $magnitude µT")
                isSpoofingDetected = true
            }
        }
        
        // Check for sensor data that doesn't match device characteristics
        if (accelerometer != null) {
            // Check for accelerometer data that suggests device is in free fall
            val zComponent = accelerometer.values[2]
            if (zComponent < -8.0 && zComponent > -12.0) {
                spoofingIndicators.add("Accelerometer suggests device in free fall")
                isSpoofingDetected = true
            }
        }
        
        return SpoofingDetectionResult(isSpoofingDetected, spoofingIndicators)
    }
    
    /**
     * Check if two sensors are correlated
     */
    private fun areSensorsCorrelated(sensor1: SensorReading, sensor2: SensorReading, threshold: Double): Boolean {
        // Simple correlation check - in a real implementation, you'd use proper correlation analysis
        return false // Placeholder for actual correlation logic
    }
    
    /**
     * Sensor event callbacks
     */
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            val sensor = sensorEvent.sensor
            val reading = SensorReading(
                sensorName = sensor.name,
                sensorType = sensor.type,
                vendor = sensor.vendor,
                version = sensor.version,
                timestamp = System.currentTimeMillis(),
                values = sensorEvent.values.toList(),
                accuracy = sensorEvent.accuracy,
                minDelay = sensor.minDelay,
                maxRange = sensor.maximumRange,
                resolution = sensor.resolution,
                power = sensor.power
            )
            
            sensorReadings[sensor.name] = reading
            updateSensorState()
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes
        sensor?.let { sensor ->
            val reading = sensorReadings[sensor.name]
            if (reading != null) {
                val updatedReading = reading.copy(accuracy = accuracy)
                sensorReadings[sensor.name] = updatedReading
                updateSensorState()
            }
        }
    }
}

/**
 * Data class for sensor readings
 */
data class SensorReading(
    val sensorName: String,
    val sensorType: Int,
    val vendor: String,
    val version: Int,
    val timestamp: Long,
    val values: List<Float>,
    val accuracy: Int,
    val minDelay: Int,
    val maxRange: Float,
    val resolution: Float,
    val power: Float
)

/**
 * Data class for sensor validation result
 */
data class SensorValidationResult(
    val isValid: Boolean,
    val issues: List<String>
)

/**
 * Data class for spoofing detection result
 */
data class SpoofingDetectionResult(
    val isSpoofingDetected: Boolean,
    val indicators: List<String>
)
