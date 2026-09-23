package com.device.spec.extractor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Screen for displaying sensor data
 */
@Composable
fun SensorDataScreen() {
    val sensorData = remember {
        listOf(
            "Accelerometer" to "Available",
            "Gyroscope" to "Available", 
            "Magnetometer" to "Available",
            "Barometer" to "Available",
            "Light Sensor" to "Available",
            "Proximity Sensor" to "Available",
            "Step Counter" to "Available",
            "Step Detector" to "Available"
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
                text = "Sensor Data",
                style = MaterialTheme.typography.titleLarge
            )
            Button(onClick = { /* Refresh sensor data */ }) {
                Text("Refresh")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Sensor list
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Available Sensors",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                sensorData.forEach { (sensor, status) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = sensor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Chip(
                            colors = if (status == "Available") {
                                ChipDefaults.chipColors(containerColor = androidx.compose.ui.graphics.Color.Green)
                            } else {
                                ChipDefaults.chipColors(containerColor = androidx.compose.ui.graphics.Color.Gray)
                            }
                        ) {
                            Text(text = status)
                        }
                    }
                }
            }
        }
        
        // Sensor readings section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Sensor Readings",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Accelerometer data
                Text(
                    text = "Accelerometer:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "X: 0.12 m/s², Y: -0.08 m/s², Z: 9.81 m/s²",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Gyroscope data
                Text(
                    text = "Gyroscope:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "X: 0.01 rad/s, Y: -0.02 rad/s, Z: 0.00 rad/s",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Magnetometer data
                Text(
                    text = "Magnetometer:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "X: 23.4 µT, Y: -12.1 µT, Z: 45.6 µT",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Sensor validation section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Sensor Validation",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Data Integrity",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    AssistChip(onClick = {}, label = { Text("Valid") })
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
                    AssistChip(onClick = {}, label = { Text("Clean") })
                }
            }
        }
    }
}