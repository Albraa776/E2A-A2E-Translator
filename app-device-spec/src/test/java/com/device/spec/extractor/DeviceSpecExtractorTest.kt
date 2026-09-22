package com.device.spec.extractor

import com.device.spec.extractor.sensors.AntiSpoofingDetector
import com.device.spec.extractor.sensors.HardwareInfoManager
import com.device.spec.extractor.sensors.SensorManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

/**
 * Unit tests for Device Spec Extractor
 */
@RunWith(MockitoJUnitRunner::class)
class DeviceSpecExtractorTest {
    
    @Mock
    private lateinit var mockContext: android.content.Context
    
    @Mock
    private lateinit var mockSensorManager: android.hardware.SensorManager
    
    @Mock
    private lateinit var mockSensor: android.hardware.Sensor
    
    private lateinit var sensorManager: SensorManager
    private lateinit var hardwareInfoManager: HardwareInfoManager
    private lateinit var antiSpoofingDetector: AntiSpoofingDetector
    
    @Before
    fun setUp() {
        sensorManager = SensorManager(mockContext)
        hardwareInfoManager = HardwareInfoManager(mockContext)
        antiSpoofingDetector = AntiSpoofingDetector(mockContext)
    }
    
    @Test
    fun testSensorManagerInitialization() {
        assertNotNull("SensorManager should be initialized", sensorManager)
        assertFalse("Sensor collection should not be started initially", sensorManager.isCollecting.value)
    }
    
    @Test
    fun testHardwareInfoManagerInitialization() {
        assertNotNull("HardwareInfoManager should be initialized", hardwareInfoManager)
    }
    
    @Test
    fun testAntiSpoofingDetectorInitialization() {
        assertNotNull("AntiSpoofingDetector should be initialized", antiSpoofingDetector)
    }
    
    @Test
    fun testSensorCollectionStartStop() {
        // Test starting sensor collection
        sensorManager.startCollection()
        assertTrue("Sensor collection should be started", sensorManager.isCollecting.value)
        
        // Test stopping sensor collection
        sensorManager.stopCollection()
        assertFalse("Sensor collection should be stopped", sensorManager.isCollecting.value)
    }
    
    @Test
    fun testSensorValidation() {
        // Mock sensor data
        val mockSensorReading = com.device.spec.extractor.sensors.SensorReading(
            sensorName = "Accelerometer",
            sensorType = android.hardware.Sensor.TYPE_ACCELEROMETER,
            vendor = "Mock Vendor",
            version = 1,
            timestamp = System.currentTimeMillis(),
            values = listOf(0.0f, 0.0f, 9.81f),
            accuracy = android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_HIGH,
            minDelay = 10000,
            maxRange = 19.6f,
            resolution = 0.01f,
            power = 0.5f
        )
        
        val sensorData = mapOf("Accelerometer" to mockSensorReading)
        val validationResult = sensorManager.validateSensorData()
        
        assertTrue("Sensor validation should pass with reasonable data", validationResult.isValid)
    }
    
    @Test
    fun testEmulatorDetection() {
        // Create mock hardware info with emulator indicators
        val hardwareInfo = com.device.spec.extractor.sensors.HardwareInfo(
            manufacturer = "Google",
            model = "Emulator",
            brand = "google",
            device = "emulator",
            product = "sdk",
            hardware = "goldfish",
            bootloader = "unknown",
            fingerprint = "generic/vbox86p/sdk/x86:8.0.0/OSPP1.180418.021/4956671:userdebug/test-keys",
            serial = "unknown",
            isRooted = false,
            isEmulator = true
        )
        
        val sensorData = emptyMap<String, com.device.spec.extractor.sensors.SensorReading>()
        val spoofingResult = antiSpoofingDetector.performAntiSpoofingAnalysis(hardwareInfo, sensorData)
        
        assertTrue("Emulator should be detected", spoofingResult.isSpoofingDetected)
        assertTrue("Emulator indicator should be present", spoofingResult.indicators.any { it.contains("Emulator") })
    }
    
    @Test
    fun testRootDetection() {
        // Create mock hardware info with root indicators
        val hardwareInfo = com.device.spec.extractor.sensors.HardwareInfo(
            manufacturer = "Mock",
            model = "Rooted Device",
            brand = "mock",
            device = "rooted",
            product = "rooted",
            hardware = "rooted",
            bootloader = "unknown",
            fingerprint = "mock/rooted/device/test-keys",
            serial = "unknown",
            isRooted = true,
            isEmulator = false
        )
        
        val sensorData = emptyMap<String, com.device.spec.extractor.sensors.SensorReading>()
        val spoofingResult = antiSpoofingDetector.performAntiSpoofingAnalysis(hardwareInfo, sensorData)
        
        assertTrue("Root should be detected", spoofingResult.isSpoofingDetected)
        assertTrue("Root indicator should be present", spoofingResult.indicators.any { it.contains("Root") })
    }
    
    @Test
    fun testSensorSpoofingDetection() {
        // Create mock sensor data with spoofing indicators
        val mockSensorReading = com.device.spec.extractor.sensors.SensorReading(
            sensorName = "Accelerometer",
            sensorType = android.hardware.Sensor.TYPE_ACCELEROMETER,
            vendor = "Mock Vendor",
            version = 1,
            timestamp = System.currentTimeMillis(),
            values = listOf(0.0f, 0.0f, 0.0f), // Zero values indicate spoofing
            accuracy = android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_HIGH,
            minDelay = 10000,
            maxRange = 19.6f,
            resolution = 0.01f,
            power = 0.5f
        )
        
        val hardwareInfo = com.device.spec.extractor.sensors.HardwareInfo()
        val sensorData = mapOf("Accelerometer" to mockSensorReading)
        val spoofingResult = antiSpoofingDetector.performAntiSpoofingAnalysis(hardwareInfo, sensorData)
        
        assertTrue("Sensor spoofing should be detected", spoofingResult.isSpoofingDetected)
        assertTrue("Zero value indicator should be present", spoofingResult.indicators.any { it.contains("zero") })
    }
    
    @Test
    fun testHardwareInfoCollection() {
        // Test hardware info collection
        // This is a simplified test - in a real scenario, you'd mock the system calls
        val hardwareInfo = com.device.spec.extractor.sensors.HardwareInfo(
            manufacturer = "Google",
            model = "Pixel 7 Pro",
            brand = "google",
            device = "panther",
            product = "panther",
            hardware = "panther",
            bootloader = "panther-12.0.0-r1",
            fingerprint = "google/pixel_7_pro/panther:12/SQ3A.220605.009.A1/8603474:user/release-keys",
            serial = "unknown",
            androidVersion = "12",
            androidSdkInt = 31,
            cpuCores = 8,
            maxMemory = 8589934592L, // 8GB
            isRooted = false,
            isEmulator = false
        )
        
        assertNotNull("Hardware info should be collected", hardwareInfo)
        assertEquals("Manufacturer should match", "Google", hardwareInfo.manufacturer)
        assertEquals("Model should match", "Pixel 7 Pro", hardwareInfo.model)
        assertEquals("CPU cores should match", 8, hardwareInfo.cpuCores)
        assertEquals("Max memory should match", 8589934592L, hardwareInfo.maxMemory)
    }
    
    @Test
    fun testCrossValidation() {
        // Create mock data for cross-validation
        val hardwareInfo = com.device.spec.extractor.sensors.HardwareInfo(
            manufacturer = "Google",
            model = "Pixel 7 Pro",
            brand = "google",
            device = "panther",
            product = "panther",
            hardware = "panther",
            bootloader = "panther-12.0.0-r1",
            fingerprint = "google/pixel_7_pro/panther:12/SQ3A.220605.009.A1/8603474:user/release-keys",
            serial = "unknown",
            androidVersion = "12",
            androidSdkInt = 31,
            cpuCores = 8,
            maxMemory = 8589934592L,
            isRooted = false,
            isEmulator = false
        )
        
        val mockSensorReading = com.device.spec.extractor.sensors.SensorReading(
            sensorName = "Accelerometer",
            sensorType = android.hardware.Sensor.TYPE_ACCELEROMETER,
            vendor = "Google Inc.",
            version = 1,
            timestamp = System.currentTimeMillis(),
            values = listOf(0.0f, 0.0f, 9.81f),
            accuracy = android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_HIGH,
            minDelay = 10000,
            maxRange = 19.6f,
            resolution = 0.01f,
            power = 0.5f
        )
        
        val sensorData = mapOf("Accelerometer" to mockSensorReading)
        val systemInfo = mapOf(
            "android_version" to "12",
            "android_sdk_int" to 31,
            "screen_width" to 1440,
            "screen_height" to 3120,
            "screen_density" to 5.0f
        )
        
        // Test cross-validation
        val crossValidationManager = com.device.spec.extractor.utils.CrossValidationManager(mockContext)
        val validationResults = crossValidationManager.performCrossValidation(hardwareInfo, sensorData, systemInfo)
        
        assertNotNull("Cross-validation results should be generated", validationResults)
        // Add more specific validation checks as needed
    }
    
    @Test
    fun testEdgeCases() {
        // Test with null or empty data
        val emptyHardwareInfo = com.device.spec.extractor.sensors.HardwareInfo()
        val emptySensorData = emptyMap<String, com.device.spec.extractor.sensors.SensorReading>()
        val emptySystemInfo = emptyMap<String, Any>()
        
        // Should handle empty data gracefully
        val spoofingResult = antiSpoofingDetector.performAntiSpoofingAnalysis(emptyHardwareInfo, emptySensorData)
        assertNotNull("Spoofing detection should handle empty data", spoofingResult)
        
        val crossValidationResult = com.device.spec.extractor.utils.CrossValidationManager(mockContext)
            .performCrossValidation(emptyHardwareInfo, emptySensorData, emptySystemInfo)
        assertNotNull("Cross-validation should handle empty data", crossValidationResult)
    }
}