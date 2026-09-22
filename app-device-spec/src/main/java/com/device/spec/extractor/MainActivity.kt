package com.device.spec.extractor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.device.spec.extractor.ui.DeviceSpecExtractorApp
import com.device.spec.extractor.ui.screens.DeviceSpecsScreen
import com.device.spec.extractor.ui.screens.HardwareInfoScreen
import com.device.spec.extractor.ui.screens.NetworkInfoScreen
import com.device.spec.extractor.ui.screens.SensorDataScreen
import com.device.spec.extractor.ui.screens.SystemInfoScreen
import com.device.spec.extractor.ui.theme.DeviceSpecExtractorTheme
import com.device.spec.extractor.viewmodels.DeviceSpecViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for Device Spec Extractor
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val viewModel: DeviceSpecViewModel by viewModels()
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            viewModel.startExtraction()
        } else {
            viewModel.handlePermissionDenied()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check and request permissions
        checkPermissions()
        
        setContent {
            DeviceSpecExtractorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DeviceSpecExtractorApp()
                }
            }
        }
    }
    
    private fun checkPermissions() {
        val permissions = mutableListOf<String>()
        
        // Add required permissions based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissions.add(Manifest.permission.READ_PHONE_STATE)
        
        // Filter permissions that are not granted
        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (notGranted.isNotEmpty()) {
            permissionLauncher.launch(notGranted.toTypedArray())
        } else {
            viewModel.startExtraction()
        }
    }
}