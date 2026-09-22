package com.device.spec.extractor.utils

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
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cross-validation utility for hardware information from multiple sources
 */
@Singleton
class CrossValidationManager @Inject constructor(
    private val context: Context
) {
    
    private val _validationResults = MutableStateFlow<CrossValidationResults>(CrossValidationResults())
    val validationResults: StateFlow<CrossValidationResults> = _validationResults
    
    /**
     * Perform cross-validation of hardware information from multiple sources
     */
    suspend fun performCrossValidation(
        hardwareInfo: HardwareInfo,
        sensorData: Map<String, SensorReading>,
        systemInfo: Map<String, Any>
    ): CrossValidationResults {
        val validationResults = CrossValidationResults()
        
        // 1. Validate build information consistency
        validateBuildInformation(hardwareInfo, validationResults)
        
        // 2. Validate sensor data consistency
        validateSensorDataConsistency(sensorData, validationResults)
        
        // 3. Validate system information consistency
        validateSystemInformationConsistency(systemInfo, hardwareInfo, validationResults)
        
        // 4. Validate hardware characteristics
        validateHardwareCharacteristics(hardwareInfo, validationResults)
        
        // 5. Validate runtime environment
        validateRuntimeEnvironment(hardwareInfo, validationResults)
        
        // 6. Validate network information
        validateNetworkInformation(validationResults)
        
        // 7. Validate device fingerprint
        validateDeviceFingerprint(hardwareInfo, validationResults)
        
        // 8. Validate memory and storage information
        validateMemoryAndStorage(hardwareInfo, validationResults)
        
        // 9. Validate CPU information
        validateCpuInformation(hardwareInfo, validationResults)
        
        // 10. Validate display information
        validateDisplayInformation(systemInfo, validationResults)
        
        _validationResults.value = validationResults
        return validationResults
    }
    
    /**
     * Validate build information consistency
     */
    private fun validateBuildInformation(hardwareInfo: HardwareInfo, results: CrossValidationResults) {
        val issues = mutableListOf<String>()
        var isValid = true
        
        // Check manufacturer and brand consistency
        if (hardwareInfo.manufacturer != hardwareInfo.brand) {
            issues.add("Manufacturer (${hardwareInfo.manufacturer}) and brand (${hardwareInfo.brand}) don't match")
            isValid = false
        }
        
        // Check model and device consistency
        if (hardwareInfo.model.contains(hardwareInfo.device, ignoreCase = false).not()) {
            issues.add("Model (${hardwareInfo.model}) and device (${hardwareInfo.device}) seem inconsistent")
            isValid = false
        }
        
        // Check fingerprint consistency
        if (!hardwareInfo.fingerprint.contains(hardwareInfo.manufacturer, ignoreCase = true) ||
            !hardwareInfo.fingerprint.contains(hardwareInfo.model, ignoreCase = true)) {
            issues.add("Fingerprint doesn't contain manufacturer or model")
            isValid = false
        }
        
        // Check for generic build information
        if (hardwareInfo.fingerprint.startsWith("generic")) {
            issues.add("Generic build fingerprint detected")
            isValid = false
        }
        
        // Check for test-keys
        if (hardwareInfo.tags.contains("test-keys")) {
            issues.add("Test keys found in build information")
            isValid = false
        }
        
        results.buildInfoValidation = BuildInfoValidation(isValid, issues)
    }
    
    /**
     * Validate sensor data consistency
     */
    private fun validateSensorDataConsistency(sensorData: Map<String, SensorReading>, results: CrossValidationResults) {
        val issues = mutableListOf<String>()
        var isValid = true
        
        // Check for critical sensors
        val criticalSensors = listOf(
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_MAGNETIC_FIELD
        )
        
        criticalSensors.forEach { sensorType ->
            val sensor = sensorData.values.find { it.sensorType == sensorType }
            if (sensor == null) {
                issues.add("Missing critical sensor: $sensorType")
                isValid = false
            } else {
                // Validate accelerometer data
                if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                    val magnitude = Math.sqrt(
                        sensor.values[0].toDouble().pow(2) +
                        sensor.values[1].toDouble().pow(2) +
                        sensor.values[2].toDouble().pow(2)
                    )
                    
                    // Earth's gravity is approximately 9.81 m/s²
                    if (magnitude < 5.0 || magnitude > 15.0) {
                        issues.add("Accelerometer magnitude unrealistic: $magnitude m/s²")
                        isValid = false
                    }
                }
                
                // Validate gyroscope data
                if (sensorType == Sensor.TYPE_GYROSCOPE) {
                    val magnitude = Math.sqrt(
                        sensor.values[0].toDouble().pow(2) +
                        sensor.values[1].toDouble().pow(2) +
                        sensor.values[2].toDouble().pow(2)
                    )
                    
                    // Gyroscope should be small when stationary
                    if (magnitude > 1.0) {
                        issues.add("Gyroscope magnitude high when stationary: $magnitude rad/s")
                        isValid = false
                    }
                }
                
                // Validate magnetometer data
                if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
                    val magnitude = Math.sqrt(
                        sensor.values[0].toDouble().pow(2) +
                        sensor.values[1].toDouble().pow(2) +
                        sensor.values[2].toDouble().pow(2)
                    )
                    
                    // Earth's magnetic field is typically 25-65 µT
                    if (magnitude < 10.0 || magnitude > 100.0) {
                        issues.add("Magnetometer magnitude unrealistic: $magnitude µT")
                        isValid = false
                    }
                }
            }
        }
        
        // Check for sensor data correlation
        val accelerometer = sensorData.values.find { it.sensorType == Sensor.TYPE_ACCELEROMETER }
        val gyroscope = sensorData.values.find { it.sensorType == Sensor.TYPE_GYROSCOPE }
        
        if (accelerometer != null && gyroscope != null) {
            // Check if sensors are perfectly correlated (unlikely in real devices)
            if (areSensorsCorrelated(accelerometer, gyroscope, 0.95)) {
                issues.add("High correlation between accelerometer and gyroscope")
                isValid = false
            }
        }
        
        results.sensorDataValidation = SensorDataValidation(isValid, issues)
    }
    
    /**
     * Validate system information consistency
     */
    private fun validateSystemInformationConsistency(
        systemInfo: Map<String, Any>,
        hardwareInfo: HardwareInfo,
        results: CrossValidationResults
    ) {
        val issues = mutableListOf<String>()
        var isValid = true
        
        // Check Android version consistency
        val androidVersion = systemInfo["android_version"] as? String ?: ""
        if (androidVersion != hardwareInfo.androidVersion) {
            issues.add("Android version mismatch: $androidVersion vs ${hardwareInfo.androidVersion}")
            isValid = false
        }
        
        // Check API level consistency
        val apiLevel = systemInfo["android_sdk_int"] as? Int ?: 0
        if (apiLevel != hardwareInfo.androidSdkInt) {
            issues.add("API level mismatch: $apiLevel vs ${hardwareInfo.androidSdkInt}")
            isValid = false
        }
        
        // Check locale consistency
        val locale = systemInfo["locale"] as? String ?: ""
        if (locale.isEmpty()) {
            issues.add("Locale information missing")
            isValid = false
        }
        
        // Check display information consistency
        val screenWidth = systemInfo["screen_width"] as? Int ?: 0
        val screenHeight = systemInfo["screen_height"] as? Int ?: 0
        
        if (screenWidth <= 0 || screenHeight <= 0) {
            issues.add("Invalid screen dimensions: ${screenWidth}x${screenHeight}")
            isValid = false
        }
        
        // Check for reasonable screen density
        val screenDensity = systemInfo["screen_density"] as? Float ?: 0f
        if (screenDensity <= 0f || screenDensity > 10f) {
            issues.add("Unreasonable screen density: $screenDensity")
            isValid = false
        }
        
        results.systemInfoValidation = SystemInfoValidation(isValid, issues)
    }
    
    /**
     * Validate hardware characteristics
     */
    private fun validateHardwareCharacteristics(hardwareInfo: HardwareInfo, results: CrossValidationResults) {
        val issues = mutableListOf<String>()
        var isValid = true
        
        // Check CPU characteristics
        if (hardwareInfo.cpuCores > 16) {
            issues.add("Unreasonable number of CPU cores: ${hardwareInfo.cpuCores}")
            isValid = false
        }
        
        // Check memory characteristics
        val maxMemoryInGB = hardwareInfo.maxMemory / (1024.0 * 1024.0 * 1024.0)
        if (maxMemoryInGB > 16.0) {
            issues.add("Unreasonable maximum memory: ${maxMemoryInGB}GB")
            isValid = false
        }
        
        // Check storage characteristics
        val storageTotalInGB = hardwareInfo.storageInfo.total / (1024.0 * 1024.0 * 1024.0)
        if (storageTotalInGB > 1000.0) {
            issues.add("Unreasonable total storage: ${storageTotalInGB}GB")
            isValid = false
        }
        
        // Check for reasonable CPU frequency
        if (hardwareInfo.cpuInfo.frequencies.isNotEmpty()) {
            hardwareInfo.cpuInfo.frequencies.forEach { frequency ->
                val freqInGHz = frequency.toDouble() / 1000.0
                if (freqInGHz < 0.5 || freqInGHz > 5.0) {
                    issues.add("Unreasonable CPU frequency: $freqInGHz GHz")
                    isValid = false
                }
            }
        }
        
        results.hardwareValidation = HardwareValidation(isValid, issues)
    }
    
    /**
     * Validate runtime environment
     */
    private fun validateRuntimeEnvironment(hardwareInfo: HardwareInfo, results: CrossValidationResults) {
        val issues = mutableListOf<String>()
        var isValid = true
        
        // Check for emulator indicators
        if (hardwareInfo.isEmulator) {
            issues.add("Device appears to be an emulator")
            isValid = false
        }
        
        // Check for root indicators
        if (hardwareInfo.isRooted) {
            issues.add("Device appears to be rooted")
            isValid = false
        }
        
        // Check for runtime environment anomalies
        val anomalies = checkRuntimeEnvironmentAnomalies()
        if (anomalies.isNotEmpty()) {
            issues.addAll(anomalies)
            isValid = false
        }
        
        results.runtimeValidation = RuntimeValidation(isValid, issues)
    }
    
    /**
     * Validate network information
     */
    private fun validateNetworkInformation(results: CrossValidationResults) {
        val issues = mutableListOf<String>()
        var isValid = true
        
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
            
            // Check for VPN
            val activeNetwork = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.type == android.net.ConnectivityManager.TYPE_VPN) {
                issues.add("VPN detected")
                isValid = false
            }
            
            // Check for proxy
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val proxy = connectivityManager.proxy
                if (proxy != null) {
                    issues.add("Proxy detected: ${proxy.host}:${proxy.port}")
                    isValid = false
                }
            }
            
        } catch (e: Exception) {
            issues.add("Error checking network information: ${e.message}")
            isValid = false
        }
        
        results.networkValidation = NetworkValidation(isValid, issues)
    }
    
    /**
     * Validate device fingerprint
     */
    private fun validateDeviceFingerprint(hardwareInfo: HardwareInfo, results: CrossValidationResults) {
        val issues = mutableListOf<String>()
        var isValid = true
        
        // Check for generic fingerprints
        if (hardwareInfo.fingerprint.startsWith("generic")) {
            issues.add("Generic build fingerprint detected")
            isValid = false
        }
        
        // Check for test-keys
        if (hardwareInfo.fingerprint.contains("test-keys")) {
            issues.add("Test keys found in build fingerprint")
            isValid = false
        }
        
        // Check for inconsistent fingerprint with build information
        if (!hardwareInfo.fingerprint.contains(hardwareInfo.manufacturer, ignoreCase = true) ||
            !hardwareInfo.fingerprint.contains(hardwareInfo.model, ignoreCase = true)) {
            issues.add("Build fingerprint doesn't match manufacturer/model")
            isValid = false
        }
        
        // Check for inconsistent build tags
        if (hardwareInfo.tags.contains("release") && hardwareInfo.fingerprint.contains("test-keys")) {
            issues.add("Release build with test keys")
            isValid = false
        }
        
        results.fingerprintValidation = FingerprintValidation(isValid, issues)
    }
    
    /**
     * Validate memory and storage information
     */
    private fun validateMemoryAndStorage(hardwareInfo: HardwareInfo, results: CrossValidationResults) {
        val issues = mutableListOf<String>()
        var isValid = true
        
        // Check memory consistency
        val totalMemory = hardwareInfo.totalMemory
        val freeMemory = hardwareInfo.freeMemory
        val maxMemory = hardwareInfo.maxMemory
        
        if (totalMemory <= 0) {
            issues.add("Invalid total memory: $totalMemory bytes")
            isValid = false
        }
        
        if (freeMemory < 0) {
            issues.add("Invalid free memory: $freeMemory bytes")
            isValid = false
        }
        
        if (maxMemory <= 0) {
            issues.add("Invalid max memory: $maxMemory bytes")
            isValid = false
        }
        
        // Check storage consistency
        val storageTotal = hardwareInfo.storageInfo.total
        val storageAvailable = hardwareInfo.storageInfo.available
        val storageUsed = hardwareInfo.storageInfo.used
        
        if (storageTotal <= 0) {
            issues.add("Invalid total storage: $storageTotal bytes")
            isValid = false
        }
        
        if (storageAvailable < 0) {
            issues.add("Invalid available storage: $storageAvailable bytes")
            isValid = false
        }
        
        if (storageUsed < 0) {
            issues.add("Invalid used storage: $storageUsed bytes")
            isValid = false
        }
        
        // Check if used storage matches total - available
        if (storageUsed != storageTotal - storageAvailable) {
            issues.add("Storage calculation inconsistency")
            isValid = false
        }
        
        results.memoryStorageValidation = MemoryStorageValidation(isValid, issues)
    }
    
    /**
     * Validate CPU information
     */
    private fun validateCpuInformation(hardwareInfo: HardwareInfo, results: CrossValidationResults) {
        val issues = mutableListOf<String>()
        var isValid = true
        
        // Check CPU core count
        if (hardwareInfo.cpuCores <= 0 || hardwareInfo.cpuCores > 16) {
            issues.add("Unreasonable CPU core count: ${hardwareInfo.cpuCores}")
            isValid = false
        }
        
        // Check CPU architecture
        if (hardwareInfo.cpuInfo.architecture.isEmpty()) {
            issues.add("CPU architecture missing")
            isValid = false
        }
        
        // Check CPU features
        if (hardwareInfo.cpuInfo.features.isEmpty()) {
            issues.add("CPU features missing")
            isValid = false
        }
        
        // Check CPU frequencies
        if (hardwareInfo.cpuInfo.frequencies.isNotEmpty()) {
            hardwareInfo.cpuInfo.frequencies.forEach { frequency ->
                val freqInGHz = frequency.toDouble() / 1000.0
                if (freqInGHz < 0.5 || freqInGHz > 5.0) {
                    issues.add("Unreasonable CPU frequency: $freqInGHz GHz")
                    isValid = false
                }
            }
        }
        
        results.cpuValidation = CpuValidation(isValid, issues)
    }
    
    /**
     * Validate display information
     */
    private fun validateDisplayInformation(systemInfo: Map<String, Any>, results: CrossValidationResults) {
        val issues = mutableListOf<String>()
        var isValid = true
        
        // Check screen dimensions
        val screenWidth = systemInfo["screen_width"] as? Int ?: 0
        val screenHeight = systemInfo["screen_height"] as? Int ?: 0
        
        if (screenWidth <= 0 || screenHeight <= 0) {
            issues.add("Invalid screen dimensions: ${screenWidth}x${screenHeight}")
            isValid = false
        }
        
        // Check for reasonable aspect ratio
        if (screenWidth > 0 && screenHeight > 0) {
            val aspectRatio = screenWidth.toDouble() / screenHeight.toDouble()
            if (aspectRatio < 0.5 || aspectRatio > 2.5) {
                issues.add("Unrealistic screen aspect ratio: $aspectRatio")
                isValid = false
            }
        }
        
        // Check screen density
        val screenDensity = systemInfo["screen_density"] as? Float ?: 0f
        if (screenDensity <= 0f || screenDensity > 10f) {
            issues.add("Unreasonable screen density: $screenDensity")
            isValid = false
        }
        
        // Check DPI
        val screenDpi = systemInfo["screen_dpi"] as? Int ?: 0
        if (screenDpi <= 0 || screenDpi > 1000) {
            issues.add("Unreasonable screen DPI: $screenDpi")
            isValid = false
        }
        
        results.displayValidation = DisplayValidation(isValid, issues)
    }
    
    /**
     * Check for runtime environment anomalies
     */
    private fun checkRuntimeEnvironmentAnomalies(): List<String> {
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
        
        return anomalies
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
 * Data class for cross-validation results
 */
data class CrossValidationResults(
    var buildInfoValidation: BuildInfoValidation = BuildInfoValidation(),
    var sensorDataValidation: SensorDataValidation = SensorDataValidation(),
    var systemInfoValidation: SystemInfoValidation = SystemInfoValidation(),
    var hardwareValidation: HardwareValidation = HardwareValidation(),
    var runtimeValidation: RuntimeValidation = RuntimeValidation(),
    var networkValidation: NetworkValidation = NetworkValidation(),
    var fingerprintValidation: FingerprintValidation = FingerprintValidation(),
    var memoryStorageValidation: MemoryStorageValidation = MemoryStorageValidation(),
    var cpuValidation: CpuValidation = CpuValidation(),
    var displayValidation: DisplayValidation = DisplayValidation()
)

/**
 * Data class for build information validation
 */
data class BuildInfoValidation(
    val isValid: Boolean = true,
    val issues: List<String> = emptyList()
)

/**
 * Data class for sensor data validation
 */
data class SensorDataValidation(
    val isValid: Boolean = true,
    val issues: List<String> = emptyList()
)

/**
 * Data class for system information validation
 */
data class SystemInfoValidation(
    val isValid: Boolean = true,
    val issues: List<String> = emptyList()
)

/**
 * Data class for hardware validation
 */
data class HardwareValidation(
    val isValid: Boolean = true,
    val issues: List<String> = emptyList()
)

/**
 * Data class for runtime validation
 */
data class RuntimeValidation(
    val isValid: Boolean = true,
    val issues: List<String> = emptyList()
)

/**
 * Data class for network validation
 */
data class NetworkValidation(
    val isValid: Boolean = true,
    val issues: List<String> = emptyList()
)

/**
 * Data class for fingerprint validation
 */
data class FingerprintValidation(
    val isValid: Boolean = true,
    val issues: List<String> = emptyList()
)

/**
 * Data class for memory and storage validation
 */
data class MemoryStorageValidation(
    val isValid: Boolean = true,
    val issues: List<String> = emptyList()
)

/**
 * Data class for CPU validation
 */
data class CpuValidation(
    val isValid: Boolean = true,
    val issues: List<String> = emptyList()
)

/**
 * Data class for display validation
 */
data class DisplayValidation(
    val isValid: Boolean = true,
    val issues: List<String> = emptyList()
)