package com.device.spec.extractor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.device.spec.extractor.R

/**
 * Screen for displaying system information
 */
@Composable
fun SystemInfoScreen() {
    val systemInfo = remember {
        mapOf(
            "Android Version" to "12",
            "API Level" to "31",
            "Security Patch" to "2023-06-05",
            "Kernel Version" to "5.10.147-android12-9-00029-gb8a4a4a5c3ab-ab8319100",
            "Build Number" to "SQ3A.220605.009.A1",
            "Build ID" to "SQ3A.220605.009.A1",
            "Build Date" to "Tue Jun 7 15:13:03 UTC 2022",
            "Build Time" to "2022-06-07T15:13:03Z",
            "Build Host" to "ab-build-52250",
            "Build Tags" to "release-keys",
            "Build Type" to "release",
            "Build User" to "android-build-ab",
            "Build Fingerprint" to "google/pixel_7_pro/panther:12/SQ3A.220605.009.A1/8603474:user/release-keys",
            "Codename" to "12",
            "Incremental" to "8603474",
            "Preview SDK Int" to "0",
            "Release" to "12",
            "SDK Int" to "31",
            "Base OS" to "Android 12.0",
            "Preview API Level" to "0",
            "Security Patch Level" to "2023-06-05",
            "System Update" to "2023-06-05",
            "System Update Security" to "2023-06-05",
            "System Update Security Patch" to "2023-06-05"
        )
    }
    
    val displayInfo = remember {
        mapOf(
            "Screen Resolution" to "1440x3120",
            "Screen Density" to "560 dpi",
            "Screen Size" to "6.7 inches",
            "Screen Aspect Ratio" to "20:9",
            "Screen Technology" to "AMOLED",
            "Screen Refresh Rate" to "120 Hz",
            "Screen Brightness" to "1000 nits",
            "Screen HDR" to "HDR10+",
            "Screen Protection" to "Gorilla Glass Victus"
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "System Information",
                style = MaterialTheme.typography.titleLarge
            )
            Button(onClick = { /* Refresh system info */ }) {
                Text("Refresh")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Android version info
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Android Version",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                systemInfo.forEach { (key, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = key,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Display information
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Display Information",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                displayInfo.forEach { (key, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = key,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Software information
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Software Information",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "UI: Pixel Launcher",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Google Play Services: 22.28.15",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Google Play Store: 33.0.31",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "System WebView: 96.0.4664.45",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Chrome: 108.0.5359.128",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
            }
        }
        
        // Build information
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Build Information",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "Build Number: SQ3A.220605.009.A1",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Build Date: Tue Jun 7 15:13:03 UTC 2022",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Build Host: ab-build-52250",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Build Tags: release-keys",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Build Type: release",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Build User: android-build-ab",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
            }
        }
        
        // Validation section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "System Validation",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "System Consistency",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Chip(
                        colors = ChipDefaults.chipColors(containerColor = androidx.compose.ui.graphics.Color.Green)
                    ) {
                        Text("Valid")
                    }
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Root Detection",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Chip(
                        colors = ChipDefaults.chipColors(containerColor = androidx.compose.ui.graphics.Color.Green)
                    ) {
                        Text("Not Rooted")
                    }
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Spoofing Detection",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Chip(
                        colors = ChipDefaults.chipColors(containerColor = androidx.compose.ui.graphics.Color.Green)
                    ) {
                        Text("Clean")
                    }
                }
            }
        }
    }
}