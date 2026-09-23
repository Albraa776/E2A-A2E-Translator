package com.device.spec.extractor.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.provider.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Anti-spoofing detection system for identifying fake or tampered devices
 */
class AntiSpoofingDetector(
    private val context: Context
) {
    
    private val _spoofingResult = MutableStateFlow<SpoofingResult>(SpoofingResult(false, emptyList()))
    val spoofingResult: StateFlow<SpoofingResult> = _spoofingResult
    
    /**
     * Perform comprehensive anti-spoofing analysis
     */
    suspend fun performAntiSpoofingAnalysis(
        hardwareInfo: HardwareInfo,
        sensorData: Map<String, SensorReading>
    ): SpoofingResult {
        val spoofingIndicators = mutableListOf<String>()
        var isSpoofingDetected = false
        
        // 1. Check for emulator indicators
        if (checkEmulatorIndicators(hardwareInfo)) {
            spoofingIndicators.add("Emulator detected")
            isSpoofingDetected = true
        }
        
        // 2. Check for root indicators
        if (checkRootIndicators(hardwareInfo)) {
            spoofingIndicators.add("Root indicators detected")
            isSpoofingDetected = true
        }
        
        // 3. Check for inconsistent build information
        if (checkBuildConsistency(hardwareInfo)) {
            spoofingIndicators.add("Inconsistent build information")
            isSpoofingDetected = true
        }
        
        // 4. Check for sensor spoofing
        if (checkSensorSpoofing(sensorData)) {
            spoofingIndicators.add("Sensor spoofing detected")
            isSpoofingDetected = true
        }
        
        // 5. Check for hardware fingerprint inconsistencies
        if (checkHardwareFingerprint(hardwareInfo)) {
            spoofingIndicators.add("Hardware fingerprint inconsistencies")
            isSpoofingDetected = true
        }
        
        // 6. Check for runtime environment anomalies
        if (checkRuntimeEnvironment()) {
            spoofingIndicators.add("Runtime environment anomalies")
            isSpoofingDetected = true
        }
        
        // 7. Check for location spoofing
        if (checkLocationSpoofing()) {
            spoofingIndicators.add("Location spoofing detected")
            isSpoofingDetected = true
        }
        
        // 8. Check for network spoofing
        if (checkNetworkSpoofing()) {
            spoofingIndicators.add("Network spoofing detected")
            isSpoofingDetected = true
        }
        
        // 9. Check for device characteristics consistency
        if (checkDeviceCharacteristics(hardwareInfo, sensorData)) {
            spoofingIndicators.add("Device characteristics inconsistencies")
            isSpoofingDetected = true
        }
        
        // 10. Check for Android build fingerprint spoofing
        if (checkAndroidBuildFingerprint(hardwareInfo)) {
            spoofingIndicators.add("Android build fingerprint spoofing")
            isSpoofingDetected = true
        }
        
        val result = SpoofingResult(isSpoofingDetected, spoofingIndicators)
        _spoofingResult.value = result
        return result
    }
    
    /**
     * Check for emulator indicators
     */
    private fun checkEmulatorIndicators(hardwareInfo: HardwareInfo): Boolean {
        val emulatorIndicators = listOf(
            hardwareInfo.fingerprint.startsWith("generic"),
            hardwareInfo.fingerprint.lowercase().contains("vbox"),
            hardwareInfo.fingerprint.lowercase().contains("test-keys"),
            hardwareInfo.model.contains("google_sdk"),
            hardwareInfo.model.contains("Emulator"),
            hardwareInfo.model.contains("Android SDK built for x86"),
            hardwareInfo.manufacturer.contains("Genymotion"),
            hardwareInfo.hardware == "goldfish",
            hardwareInfo.hardware == "ranchu",
            hardwareInfo.product == "sdk",
            hardwareInfo.product == "google_sdk",
            hardwareInfo.product == "sdk_x86",
            hardwareInfo.product == "vbox86p",
            hardwareInfo.board.lowercase().contains("nox"),
            hardwareInfo.bootloader.lowercase().contains("nox"),
            hardwareInfo.brand.lowercase().contains("nox"),
            hardwareInfo.hardware.lowercase().contains("nox"),
            hardwareInfo.product.lowercase().contains("nox"),
            hardwareInfo.serial == "unknown",
            hardwareInfo.serial == "null"
        )
        
        return emulatorIndicators.any { it }
    }
    
    /**
     * Check for root indicators
     */
    private fun checkRootIndicators(hardwareInfo: HardwareInfo): Boolean {
        val rootPaths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        
        rootPaths.forEach { path ->
            if (File(path).exists()) {
                return true
            }
        }
        
        // Check for root apps
        val rootApps = arrayOf(
            "com.noshufou.android.su",
            "com.thirdparty.superuser",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.zachspong.temprootremovejb",
            "com.ramdroid.appquarantine",
            "com.topjohnwu.magisk"
        )
        
        rootApps.forEach { app ->
            if (context.packageManager.getInstalledApplications(0).any { it.packageName == app }) {
                return true
            }
        }
        
        // Check for su binary in PATH
        try {
            val process = Runtime.getRuntime().exec("which su")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val result = reader.readLine()
            if (result != null) {
                return true
            }
        } catch (e: Exception) {
            // Ignore exceptions
        }
        
        // Check for build tags containing test-keys
        if (hardwareInfo.tags.contains("test-keys")) {
            return true
        }
        
        return false
    }
    
    /**
     * Check for build consistency
     */
    private fun checkBuildConsistency(hardwareInfo: HardwareInfo): Boolean {
        val inconsistencies = mutableListOf<String>()
        
        // Check manufacturer and brand consistency
        if (hardwareInfo.manufacturer != hardwareInfo.brand) {
            inconsistencies.add("Manufacturer (${hardwareInfo.manufacturer}) and brand (${hardwareInfo.brand}) don't match")
        }
        
        // Check model and device consistency
        if (hardwareInfo.model.contains(hardwareInfo.device).not()) {
            inconsistencies.add("Model (${hardwareInfo.model}) and device (${hardwareInfo.device}) seem inconsistent")
        }
        
        // Check fingerprint consistency
        if (hardwareInfo.fingerprint.contains(hardwareInfo.manufacturer).not() ||
            hardwareInfo.fingerprint.contains(hardwareInfo.model).not()) {
            inconsistencies.add("Fingerprint doesn't contain manufacturer or model")
        }
        
        // Check CPU architecture consistency
        if (hardwareInfo.supported64BitAbis.isNotEmpty() && hardwareInfo.cpuInfo.architecture != "8") {
            inconsistencies.add("64-bit ABI support but CPU architecture suggests 32-bit")
        }
        
        return inconsistencies.isNotEmpty()
    }
    
    /**
     * Check for sensor spoofing
     */
    private fun checkSensorSpoofing(sensorData: Map<String, SensorReading>): Boolean {
        val spoofingIndicators = mutableListOf<String>()
        
        // Check accelerometer data
        val accelerometer = sensorData.values.find { it.sensorType == Sensor.TYPE_ACCELEROMETER }
        if (accelerometer != null) {
            val magnitude = Math.sqrt(
                accelerometer.values[0].toDouble().pow(2) +
                accelerometer.values[1].toDouble().pow(2) +
                accelerometer.values[2].toDouble().pow(2)
            )
            
            // Earth's gravity is approximately 9.81 m/s²
            if (magnitude < 5.0 || magnitude > 15.0) {
                spoofingIndicators.add("Accelerometer magnitude unrealistic: $magnitude m/s²")
            }
            
            // Check for zero values (might indicate spoofing)
            if (accelerometer.values.any { it == 0.0f }) {
                spoofingIndicators.add("Accelerometer contains zero values")
            }
        }
        
        // Check gyroscope data
        val gyroscope = sensorData.values.find { it.sensorType == Sensor.TYPE_GYROSCOPE }
        if (gyroscope != null) {
            val magnitude = Math.sqrt(
                gyroscope.values[0].toDouble().pow(2) +
                gyroscope.values[1].toDouble().pow(2) +
                gyroscope.values[2].toDouble().pow(2)
            )
            
            // Gyroscope should be small when stationary
            if (magnitude > 1.0) {
                spoofingIndicators.add("Gyroscope magnitude high when stationary: $magnitude rad/s")
            }
        }
        
        // Check magnetometer data
        val magnetometer = sensorData.values.find { it.sensorType == Sensor.TYPE_MAGNETIC_FIELD }
        if (magnetometer != null) {
            val magnitude = Math.sqrt(
                magnetometer.values[0].toDouble().pow(2) +
                magnetometer.values[1].toDouble().pow(2) +
                magnetometer.values[2].toDouble().pow(2)
            )
            
            // Earth's magnetic field is typically 25-65 µT
            if (magnitude < 10.0 || magnitude > 100.0) {
                spoofingIndicators.add("Magnetometer magnitude unrealistic: $magnitude µT")
            }
        }
        
        // Check for sensor data correlation (might indicate spoofing)
        if (accelerometer != null && gyroscope != null) {
            // Check if sensors are perfectly correlated (unlikely in real devices)
            if (areSensorsCorrelated(accelerometer, gyroscope, 0.95)) {
                spoofingIndicators.add("High correlation between accelerometer and gyroscope")
            }
        }
        
        return spoofingIndicators.isNotEmpty()
    }
    
    /**
     * Check for hardware fingerprint inconsistencies
     */
    private fun checkHardwareFingerprint(hardwareInfo: HardwareInfo): Boolean {
        val inconsistencies = mutableListOf<String>()
        
        // Check for generic fingerprints
        if (hardwareInfo.fingerprint.contains("generic")) {
            inconsistencies.add("Generic fingerprint detected")
        }
        
        // Check for test-keys in fingerprint
        if (hardwareInfo.fingerprint.contains("test-keys")) {
            inconsistencies.add("Test keys found in fingerprint")
        }
        
        // Check for inconsistent fingerprint with manufacturer/model
        if (!hardwareInfo.fingerprint.contains(hardwareInfo.manufacturer) ||
            !hardwareInfo.fingerprint.contains(hardwareInfo.model)) {
            inconsistencies.add("Fingerprint doesn't match manufacturer/model")
        }
        
        // Check for bootloader inconsistencies
        if (hardwareInfo.bootloader.contains("unknown")) {
            inconsistencies.add("Unknown bootloader detected")
        }
        
        return inconsistencies.isNotEmpty()
    }
    
    /**
     * Check for runtime environment anomalies
     */
    private fun checkRuntimeEnvironment(): Boolean {
        val anomalies = mutableListOf<String>()
        
        // Check for emulator-specific files
        val emulatorFiles = arrayOf(
            "/system/lib/libc_malloc_debug_qemu.so",
            "/sys/qemu_trace",
            "/system/bin/qemu-props",
            "/dev/socket/qemud",
            "/dev/qemu_pipe",
            "/proc/tty/drivers"
        )
        
        emulatorFiles.forEach { file ->
            if (File(file).exists()) {
                anomalies.add("Emulator-specific file found: $file")
            }
        }
        
        // Check for emulator-specific properties
        try {
            val properties = arrayOf(
                "ro.kernel.qemu",
                "ro.qemu",
                "ro.boot.qemu",
                "ro.product.model"
            )
            
            properties.forEach { prop ->
                val value = Settings.System.getString(context.contentResolver, prop)
                if (value != null) {
                    anomalies.add("Emulator property detected: $prop=$value")
                }
            }
        } catch (e: Exception) {
            // Ignore exceptions
        }
        
        // Check for running in emulator
        if (Build.FINGERPRINT.startsWith("generic") ||
            Build.MODEL.contains("google_sdk") ||
            Build.MODEL.contains("Emulator")) {
            anomalies.add("Running in emulator environment")
        }
        
        return anomalies.isNotEmpty()
    }
    
    /**
     * Check for location spoofing
     */
    private fun checkLocationSpoofing(): Boolean {
        val spoofingIndicators = mutableListOf<String>()
        
        try {
            // Check for mock location enabled
            val mockLocation = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ALLOW_MOCK_LOCATION,
                0
            )
            
            if (mockLocation == 1) {
                spoofingIndicators.add("Mock location enabled")
            }
            
            // Check for mock location apps
            val mockLocationApps = arrayOf(
                "com.android.development",
                "com.android.development_settings",
                "com.android.customlocale2",
                "com.android.spare_parts"
            )
            
            mockLocationApps.forEach { app ->
                if (context.packageManager.getInstalledApplications(0).any { it.packageName == app }) {
                    spoofingIndicators.add("Mock location app detected: $app")
                }
            }
            
        } catch (e: Exception) {
            // Ignore exceptions
        }
        
        return spoofingIndicators.isNotEmpty()
    }
    
    /**
     * Check for network spoofing
     */
    private fun checkNetworkSpoofing(): Boolean {
        val spoofingIndicators = mutableListOf<String>()
        
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
            
            // Check for VPN
            val activeNetwork = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.type == android.net.ConnectivityManager.TYPE_VPN) {
                spoofingIndicators.add("VPN detected")
            }
            
            // Check for proxy
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val proxy = connectivityManager.proxy
                if (proxy != null) {
                    spoofingIndicators.add("Proxy detected: ${proxy.host}:${proxy.port}")
                }
            }
            
        } catch (e: Exception) {
            // Ignore exceptions
        }
        
        return spoofingIndicators.isNotEmpty()
    }
    
    /**
     * Check for device characteristics consistency
     */
    private fun checkDeviceCharacteristics(
        hardwareInfo: HardwareInfo,
        sensorData: Map<String, SensorReading>
    ): Boolean {
        val inconsistencies = mutableListOf<String>()
        
        // Check CPU characteristics against expected values
        if (hardwareInfo.cpuCores > 16) {
            inconsistencies.add("Unreasonable number of CPU cores: ${hardwareInfo.cpuCores}")
        }
        
        // Check memory characteristics
        val maxMemoryInGB = hardwareInfo.maxMemory / (1024.0 * 1024.0 * 1024.0)
        if (maxMemoryInGB > 16.0) {
            inconsistencies.add("Unreasonable maximum memory: ${maxMemoryInGB}GB")
        }
        
        // Check screen resolution against device class
        val screenWidth = hardwareInfo.additionalInfo["screen_width"]?.toIntOrNull() ?: 0
        val screenHeight = hardwareInfo.additionalInfo["screen_height"]?.toIntOrNull() ?: 0
        
        if (screenWidth > 0 && screenHeight > 0) {
            val aspectRatio = screenWidth.toDouble() / screenHeight.toDouble()
            
            // Check for unrealistic aspect ratios
            if (aspectRatio < 0.5 || aspectRatio > 2.5) {
                inconsistencies.add("Unrealistic screen aspect ratio: $aspectRatio")
            }
        }
        
        // Check sensor availability against device type
        val hasAccelerometer = sensorData.values.any { it.sensorType == Sensor.TYPE_ACCELEROMETER }
        val hasGyroscope = sensorData.values.any { it.sensorType == Sensor.TYPE_GYROSCOPE }
        val hasMagnetometer = sensorData.values.any { it.sensorType == Sensor.TYPE_MAGNETIC_FIELD }
        
        // Most smartphones should have these sensors
        if (hardwareInfo.model.contains("phone", ignoreCase = true)) {
            if (!hasAccelerometer) {
                inconsistencies.add("Smartphone missing accelerometer")
            }
            if (!hasGyroscope) {
                inconsistencies.add("Smartphone missing gyroscope")
            }
        }
        
        return inconsistencies.isNotEmpty()
    }
    
    /**
     * Check for Android build fingerprint spoofing
     */
    private fun checkAndroidBuildFingerprint(hardwareInfo: HardwareInfo): Boolean {
        val spoofingIndicators = mutableListOf<String>()
        
        // Check for generic build fingerprints
        if (hardwareInfo.fingerprint.contains("generic")) {
            spoofingIndicators.add("Generic build fingerprint detected")
        }
        
        // Check for test-keys
        if (hardwareInfo.fingerprint.contains("test-keys")) {
            spoofingIndicators.add("Test keys found in build fingerprint")
        }
        
        // Check for inconsistent fingerprint with build information
        if (hardwareInfo.fingerprint.contains(hardwareInfo.manufacturer).not() ||
            hardwareInfo.fingerprint.contains(hardwareInfo.model).not()) {
            spoofingIndicators.add("Build fingerprint doesn't match manufacturer/model")
        }
        
        // Check for inconsistent build tags
        if (hardwareInfo.tags.contains("release") && hardwareInfo.fingerprint.contains("test-keys")) {
            spoofingIndicators.add("Release build with test keys")
        }
        
        return spoofingIndicators.isNotEmpty()
    }
    
    /**
     * Check if two sensors are correlated
     */
    private fun areSensorsCorrelated(sensor1: SensorReading, sensor2: SensorReading, threshold: Double): Boolean {
        // Simple correlation check - in a real implementation, you'd use proper correlation analysis
        // For now, return false as a placeholder
        return false
    }
}

/**
 * Data class for spoofing detection result
 */
data class SpoofingResult(
    val isSpoofingDetected: Boolean,
    val indicators: List<String>
)