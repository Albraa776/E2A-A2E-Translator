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
 * Main screen for device specification extraction
 */
@Composable
fun DeviceSpecsScreen(
    onNavigateToSensors: () -> Unit,
    onNavigateToHardware: () -> Unit,
    onNavigateToSystem: () -> Unit,
    onNavigateToNetwork: () -> Unit
) {
    var isExtracting by remember { mutableStateOf(false) }
    var extractionProgress by remember { mutableFloatStateOf(0f) }
    var extractionStatus by remember { mutableStateOf("Ready to extract device specifications") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Device Specification Extractor",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Status card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = extractionStatus,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (isExtracting) {
                    LinearProgressIndicator(
                        progress = extractionProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                }
            }
        }
        
        // Action buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    isExtracting = true
                    extractionProgress = 0.2f
                    extractionStatus = "Collecting sensor data..."
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isExtracting
            ) {
                Text(text = "Extract Specifications")
            }
            
            Button(
                onClick = onNavigateToSensors,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isExtracting
            ) {
                Text(text = "View Sensor Data")
            }
            
            Button(
                onClick = onNavigateToHardware,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isExtracting
            ) {
                Text(text = "View Hardware Info")
            }
            
            Button(
                onClick = onNavigateToSystem,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isExtracting
            ) {
                Text(text = "View System Info")
            }
            
            Button(
                onClick = onNavigateToNetwork,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isExtracting
            ) {
                Text(text = "View Network Info")
            }
        }
        
        // Information section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "This app extracts real device hardware specifications even if the OS is faked. " +
                           "It uses multiple sources including sensors, hardware information, and system data " +
                           "to provide accurate device identification.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}