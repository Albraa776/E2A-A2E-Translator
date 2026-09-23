package com.device.spec.extractor.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.device.spec.extractor.ui.screens.DeviceSpecsScreen
import com.device.spec.extractor.ui.screens.HardwareInfoScreen
import com.device.spec.extractor.ui.screens.NetworkInfoScreen
import com.device.spec.extractor.ui.screens.SensorDataScreen
import com.device.spec.extractor.ui.screens.SystemInfoScreen
import com.device.spec.extractor.ui.theme.DeviceSpecExtractorTheme

/**
 * Main composable function for Device Spec Extractor app
 */
@Composable
fun DeviceSpecExtractorApp() {
    val navController = rememberNavController()
    
    DeviceSpecExtractorTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = "device_specs"
            ) {
                composable("device_specs") {
                    DeviceSpecsScreen(
                        onNavigateToSensors = { navController.navigate("sensor_data") },
                        onNavigateToHardware = { navController.navigate("hardware_info") },
                        onNavigateToSystem = { navController.navigate("system_info") },
                        onNavigateToNetwork = { navController.navigate("network_info") }
                    )
                }
                
                composable("sensor_data") {
                    SensorDataScreen()
                }
                
                composable("hardware_info") {
                    HardwareInfoScreen()
                }
                
                composable("system_info") {
                    SystemInfoScreen()
                }
                
                composable("network_info") {
                    NetworkInfoScreen()
                }
            }
        }
    }
}