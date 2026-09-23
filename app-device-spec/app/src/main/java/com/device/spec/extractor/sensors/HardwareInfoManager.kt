package com.device.spec.extractor.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Hardware information manager for collecting and validating hardware data
 */
class HardwareInfoManager(
    private val context: Context
) {
    
    private val _hardwareInfo = MutableStateFlow<HardwareInfo>(HardwareInfo())
    val hardwareInfo: StateFlow<HardwareInfo> = _hardwareInfo
    
    private val _validationResult = MutableStateFlow<HardwareValidationResult>(HardwareValidationResult(true, emptyList()))
    val validationResult: StateFlow<HardwareValidationResult> = _validationResult
    
    /**
     * Collect hardware information
     */
    suspend fun collectHardwareInfo() {
        val hardwareInfo = HardwareInfo(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            brand = Build.BRAND,
            device = Build.DEVICE,
            product = Build.PRODUCT,
            hardware = Build.HARDWARE,
            board = Build.BOARD,
            bootloader = Build.BOOTLOADER,
            fingerprint = Build.FINGERPRINT,
            serial = Build.SERIAL,
            display = Build.DISPLAY,
            tags = Build.TAGS,
            type = Build.TYPE,
            time = Build.TIME,
            user = Build.USER,
            host = Build.HOST,
            id = Build.ID,
            supportedAbis = Build.SUPPORTED_ABIS,
            supported32BitAbis = Build.SUPPORTED_32_BIT_ABIS,
            supported64BitAbis = Build.SUPPORTED_64_BIT_ABIS,
            androidVersion = Build.VERSION.RELEASE,
            androidSdkInt = Build.VERSION.SDK_INT,
            androidCodename = Build.VERSION.CODENAME,
            androidIncremental = Build.VERSION.INCREMENTAL,
            androidSecurityPatch = Build.VERSION.SECURITY_PATCH,
            cpuCores = Runtime.getRuntime().availableProcessors(),
            maxMemory = Runtime.getRuntime().maxMemory(),
            totalMemory = Runtime.getRuntime().totalMemory(),
            freeMemory = Runtime.getRuntime().freeMemory(),
            isRooted = checkRoot(),
            isEmulator = checkEmulator(),
            cpuInfo = getCpuInfo(),
            memoryInfo = getMemoryInfo(),
            storageInfo = getStorageInfo(),
            sensorInfo = getSensorInfo(),
            additionalInfo = getAdditionalInfo()
        )
        
        _hardwareInfo.value = hardwareInfo
        validateHardwareInfo(hardwareInfo)
    }
    
    /**
     * Validate hardware information
     */
    private fun validateHardwareInfo(hardwareInfo: HardwareInfo) {
        val issues = mutableListOf<String>()
        var isValid = true
        
        // Check for root indicators
        if (hardwareInfo.isRooted) {
            issues.add("Device appears to be rooted")
            isValid = false
        }
        
        // Check for emulator indicators
        if (hardwareInfo.isEmulator) {
            issues.add("Device appears to be an emulator")
            isValid = false
        }
        
        // Check for inconsistent build information
        if (hardwareInfo.manufacturer.isEmpty() || hardwareInfo.model.isEmpty()) {
            issues.add("Incomplete manufacturer/model information")
            isValid = false
        }
        
        // Check for unrealistic hardware combinations
        if (hardwareInfo.cpuCores > 16) {
            issues.add("Unreasonable number of CPU cores: ${hardwareInfo.cpuCores}")
            isValid = false
        }
        
        // Check memory information
        val maxMemoryInGB = hardwareInfo.maxMemory / (1024.0 * 1024.0 * 1024.0)
        if (maxMemoryInGB > 16.0) {
            issues.add("Unreasonable maximum memory: ${maxMemoryInGB}GB")
            isValid = false
        }
        
        // Check sensor information
        val criticalSensors = listOf(
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_MAGNETIC_FIELD
        )
        
        criticalSensors.forEach { sensorType ->
            val sensor = hardwareInfo.sensorInfo.find { it.type == sensorType }
            if (sensor == null) {
                issues.add("Missing critical sensor: $sensorType")
                isValid = false
            }
        }
        
        // Check for fingerprint spoofing
        if (hardwareInfo.fingerprint.contains("generic") || 
            hardwareInfo.fingerprint.contains("vbox") || 
            hardwareInfo.fingerprint.contains("test-keys")) {
            issues.add("Suspicious fingerprint: ${hardwareInfo.fingerprint}")
            isValid = false
        }
        
        // Check for bootloader spoofing
        if (hardwareInfo.bootloader.contains("unknown")) {
            issues.add("Unknown bootloader: ${hardwareInfo.bootloader}")
            isValid = false
        }
        
        // Check for serial number spoofing
        if (hardwareInfo.serial == "unknown" || hardwareInfo.serial == "null") {
            issues.add("Suspicious serial number: ${hardwareInfo.serial}")
            isValid = false
        }
        
        _validationResult.value = HardwareValidationResult(isValid, issues)
    }
    
    /**
     * Check for root indicators
     */
    private fun checkRoot(): Boolean {
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
        
        return false
    }
    
    /**
     * Check for emulator indicators
     */
    private fun checkEmulator(): Boolean {
        return Build.FINGERPRINT.startsWith("generic") ||
               Build.FINGERPRINT.toLowerCase().contains("vbox") ||
               Build.FINGERPRINT.toLowerCase().contains("test-keys") ||
               Build.MODEL.contains("google_sdk") ||
               Build.MODEL.contains("Emulator") ||
               Build.MODEL.contains("Android SDK built for x86") ||
               Build.MANUFACTURER.contains("Genymotion") ||
               Build.HARDWARE == "goldfish" ||
               Build.HARDWARE == "ranchu" ||
               Build.PRODUCT == "sdk" ||
               Build.PRODUCT == "google_sdk" ||
               Build.PRODUCT == "sdk_x86" ||
               Build.PRODUCT == "vbox86p" ||
               Build.BOARD.lowercase().contains("nox") ||
               Build.BOOTLOADER.lowercase().contains("nox") ||
               Build.BRAND.lowercase().contains("nox") ||
               Build.HARDWARE.lowercase().contains("nox") ||
               Build.PRODUCT.lowercase().contains("nox") ||
               Build.SERIAL == "unknown" ||
               Build.SERIAL == "null"
    }
    
    /**
     * Get CPU information
     */
    private fun getCpuInfo(): CpuInfo {
        val cpuInfo = CpuInfo()
        
        try {
            val reader = BufferedReader(InputStreamReader(File("/proc/cpuinfo").inputStream()))
            var line: String?
            var cores = 0
            var frequencies = mutableListOf<String>()
            
            while (reader.readLine().also { line = it } != null) {
                line?.let {
                    if (it.startsWith("processor")) {
                        cores++
                    } else if (it.startsWith("cpu MHz")) {
                        val frequency = it.split(":")[1].trim()
                        frequencies.add(frequency)
                    } else if (it.startsWith("Features")) {
                        cpuInfo.features = it.split(":")[1].trim()
                    } else if (it.startsWith("CPU implementer")) {
                        cpuInfo.implementer = it.split(":")[1].trim()
                    } else if (it.startsWith("CPU architecture")) {
                        cpuInfo.architecture = it.split(":")[1].trim()
                    } else if (it.startsWith("CPU variant")) {
                        cpuInfo.variant = it.split(":")[1].trim()
                    } else if (it.startsWith("CPU part")) {
                        cpuInfo.part = it.split(":")[1].trim()
                    } else if (it.startsWith("CPU revision")) {
                        cpuInfo.revision = it.split(":")[1].trim()
                    }
                }
            }
            
            cpuInfo.cores = cores
            cpuInfo.frequencies = frequencies
            
        } catch (e: Exception) {
            // Ignore exceptions
        }
        
        return cpuInfo
    }
    
    /**
     * Get memory information
     */
    private fun getMemoryInfo(): MemoryInfo {
        val memoryInfo = MemoryInfo()
        
        try {
            val reader = BufferedReader(InputStreamReader(File("/proc/meminfo").inputStream()))
            var line: String?
            
            while (reader.readLine().also { line = it } != null) {
                line?.let {
                    if (it.startsWith("MemTotal:")) {
                        memoryInfo.total = it.split(":")[1].trim().split(" ")[0].toLong()
                    } else if (it.startsWith("MemFree:")) {
                        memoryInfo.free = it.split(":")[1].trim().split(" ")[0].toLong()
                    } else if (it.startsWith("MemAvailable:")) {
                        memoryInfo.available = it.split(":")[1].trim().split(" ")[0].toLong()
                    } else if (it.startsWith("Buffers:")) {
                        memoryInfo.buffers = it.split(":")[1].trim().split(" ")[0].toLong()
                    } else if (it.startsWith("Cached:")) {
                        memoryInfo.cached = it.split(":")[1].trim().split(" ")[0].toLong()
                    }
                }
            }
            
        } catch (e: Exception) {
            // Ignore exceptions
        }
        
        return memoryInfo
    }
    
    /**
     * Get storage information
     */
    private fun getStorageInfo(): StorageInfo {
        val storageInfo = StorageInfo()
        
        try {
            val stat = android.os.StatFs(context.filesDir.path)
            val blockSize = stat.blockSizeLong
            val availableBlocks = stat.availableBlocksLong
            val totalBlocks = stat.blockCountLong
            
            storageInfo.total = totalBlocks * blockSize
            storageInfo.available = availableBlocks * blockSize
            storageInfo.used = (totalBlocks - availableBlocks) * blockSize
            
        } catch (e: Exception) {
            // Ignore exceptions
        }
        
        return storageInfo
    }
    
    /**
     * Get sensor information
     */
    private fun getSensorInfo(): List<SensorInfo> {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
        
        return sensors.map { sensor ->
            SensorInfo(
                name = sensor.name,
                type = sensor.type,
                vendor = sensor.vendor,
                version = sensor.version,
                resolution = sensor.resolution,
                power = sensor.power,
                maxRange = sensor.maximumRange,
                minDelay = sensor.minDelay,
                fifoMaxEventCount = sensor.fifoMaxEventCount,
                fifoReservedEventCount = sensor.fifoReservedEventCount,
                batchDelay = 0,
                batchTimeout = 0,
                isWakeUpSensor = sensor.isWakeUpSensor,
                isDynamicSensor = sensor.isDynamicSensor
            )
        }
    }
    
    /**
     * Get additional information
     */
    private fun getAdditionalInfo(): Map<String, String> {
        val additionalInfo = mutableMapOf<String, String>()
        
        // Get Android ID
        try {
            val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            additionalInfo["android_id"] = androidId ?: "unknown"
        } catch (e: Exception) {
            additionalInfo["android_id"] = "unknown"
        }
        
        // Get locale
        additionalInfo["locale"] = context.resources.configuration.locale.toString()
        
        // Get display metrics
        val displayMetrics = context.resources.displayMetrics
        additionalInfo["screen_width"] = displayMetrics.widthPixels.toString()
        additionalInfo["screen_height"] = displayMetrics.heightPixels.toString()
        additionalInfo["screen_density"] = displayMetrics.density.toString()
        additionalInfo["screen_dpi"] = displayMetrics.densityDpi.toString()
        
        // Get timezone
        additionalInfo["timezone"] = java.util.TimeZone.getDefault().id
        
        // Get MAC address (requires location permission)
        try {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as android.net.wifi.WifiManager
            val connectionInfo = wifiManager.connectionInfo
            additionalInfo["mac_address"] = connectionInfo.macAddress
        } catch (e: Exception) {
            additionalInfo["mac_address"] = "unknown"
        }
        
        return additionalInfo
    }
}

/**
 * Data class for hardware information
 */
data class HardwareInfo(
    val manufacturer: String = "",
    val model: String = "",
    val brand: String = "",
    val device: String = "",
    val product: String = "",
    val hardware: String = "",
    val board: String = "",
    val bootloader: String = "",
    val fingerprint: String = "",
    val serial: String = "",
    val display: String = "",
    val tags: String = "",
    val type: String = "",
    val time: Long = 0,
    val user: String = "",
    val host: String = "",
    val id: String = "",
    val supportedAbis: Array<String> = emptyArray(),
    val supported32BitAbis: Array<String> = emptyArray(),
    val supported64BitAbis: Array<String> = emptyArray(),
    val androidVersion: String = "",
    val androidSdkInt: Int = 0,
    val androidCodename: String = "",
    val androidIncremental: String = "",
    val androidSecurityPatch: String = "",
    val cpuCores: Int = 0,
    val maxMemory: Long = 0,
    val totalMemory: Long = 0,
    val freeMemory: Long = 0,
    val isRooted: Boolean = false,
    val isEmulator: Boolean = false,
    val cpuInfo: CpuInfo = CpuInfo(),
    val memoryInfo: MemoryInfo = MemoryInfo(),
    val storageInfo: StorageInfo = StorageInfo(),
    val sensorInfo: List<SensorInfo> = emptyList(),
    val additionalInfo: Map<String, String> = emptyMap()
)

/**
 * Data class for CPU information
 */
data class CpuInfo(
    var cores: Int = 0,
    var frequencies: List<String> = emptyList(),
    var features: String = "",
    var implementer: String = "",
    var architecture: String = "",
    var variant: String = "",
    var part: String = "",
    var revision: String = ""
)

/**
 * Data class for memory information
 */
data class MemoryInfo(
    var total: Long = 0,
    var free: Long = 0,
    var available: Long = 0,
    var buffers: Long = 0,
    var cached: Long = 0
)

/**
 * Data class for storage information
 */
data class StorageInfo(
    var total: Long = 0,
    var available: Long = 0,
    var used: Long = 0
)

/**
 * Data class for sensor information
 */
data class SensorInfo(
    val name: String,
    val type: Int,
    val vendor: String,
    val version: Int,
    val resolution: Float,
    val power: Float,
    val maxRange: Float,
    val minDelay: Int,
    val fifoMaxEventCount: Int,
    val fifoReservedEventCount: Int,
    val batchDelay: Int,
    val batchTimeout: Int,
    val isWakeUpSensor: Boolean,
    val isDynamicSensor: Boolean
)

/**
 * Data class for hardware validation result
 */
data class HardwareValidationResult(
    val isValid: Boolean,
    val issues: List<String>
)