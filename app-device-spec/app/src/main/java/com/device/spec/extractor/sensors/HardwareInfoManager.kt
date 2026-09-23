package com.device.spec.extractor.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Hardware information manager for collecting and validating hardware data
 * (Simplified safe-API version for build isolation)
 */
class HardwareInfoManager(
    private val context: Context
) {

    private val _hardwareInfo = MutableStateFlow(HardwareInfo())
    val hardwareInfo: StateFlow<HardwareInfo> = _hardwareInfo

    private val _validationResult = MutableStateFlow(HardwareValidationResult(true, emptyList()))
    val validationResult: StateFlow<HardwareValidationResult> = _validationResult

    /**
     * Collect hardware information (safe APIs only)
     */
    suspend fun collectHardwareInfo() {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
        val sensorInfo = sensors.map { sensor ->
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
            serial = "unknown",
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
            androidVersion = Build.VERSION.RELEASE ?: "unknown",
            androidSdkInt = Build.VERSION.SDK_INT,
            androidCodename = Build.VERSION.CODENAME ?: "unknown",
            androidIncremental = Build.VERSION.INCREMENTAL ?: "unknown",
            androidSecurityPatch = Build.VERSION.SECURITY_PATCH ?: "unknown",
            cpuCores = Runtime.getRuntime().availableProcessors(),
            maxMemory = Runtime.getRuntime().maxMemory(),
            totalMemory = Runtime.getRuntime().totalMemory(),
            freeMemory = Runtime.getRuntime().freeMemory(),
            isRooted = false,
            isEmulator = checkEmulator(),
            cpuInfo = CpuInfo(cores = Runtime.getRuntime().availableProcessors()),
            memoryInfo = MemoryInfo(),
            storageInfo = StorageInfo(),
            sensorInfo = sensorInfo,
            additionalInfo = emptyMap()
        )

        _hardwareInfo.value = hardwareInfo
        _validationResult.value = HardwareValidationResult(true, emptyList())
    }

    /**
     * Check for emulator indicators (Build fields only)
     */
    private fun checkEmulator(): Boolean {
        return Build.FINGERPRINT.startsWith("generic") ||
            Build.MODEL.contains("google_sdk") ||
            Build.MODEL.contains("Emulator") ||
            Build.HARDWARE == "goldfish" ||
            Build.HARDWARE == "ranchu"
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
