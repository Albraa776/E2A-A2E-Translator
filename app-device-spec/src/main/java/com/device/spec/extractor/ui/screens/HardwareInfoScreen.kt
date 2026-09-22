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
 * Screen for displaying hardware information
 */
@Composable
fun HardwareInfoScreen() {
    val hardwareInfo = remember {
        mapOf(
            "Manufacturer" to "Google",
            "Model" to "Pixel 7 Pro",
            "Brand" to "google",
            "Device" to "panther",
            "Product" to "panther",
            "Hardware" to "panther",
            "Board" to "cloudripper",
            "Bootloader" to "12.0.0_r1",
            "Fingerprint" to "google/pixel_7_pro/panther:12/SQ3A.220605.009.A1/8603474:user/release-keys",
            "Security Patch" to "2023-06-05",
            "CPU ABI" to "arm64-v8a",
            "CPU ABI2" to "",
            "CPU Features" to "fp asimd evtstrm crc32 cpuid",
            "CPU Architecture" to "ARMv8",
            "CPU Cores" to "8",
            "CPU Max Frequency" to "2.85 GHz",
            "CPU Min Frequency" to "1.80 GHz"
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
                text = "Hardware Information",
                style = MaterialTheme.typography.titleLarge
            )
            Button(onClick = { /* Refresh hardware info */ }) {
                Text("Refresh")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Basic hardware info
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Basic Hardware Info",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                hardwareInfo.forEach { (key, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
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
        
        // CPU information
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "CPU Information",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "Processor: Google Tensor G2",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "Manufacturing Process: 4nm",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "GPU: ARM Mali-G710 MP7",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "ISP: Triple ISP",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                // CPU cores breakdown
                Text(
                    text = "CPU Cores:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 4.dp)
                )
                Text(
                    text = "1x Prime (2.85 GHz) + 3x Gold (2.35 GHz) + 4x Silver (1.80 GHz)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Memory information
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Memory Information",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "RAM: 12 GB",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Storage: 128 GB / 256 GB / 512 GB",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Storage Type: UFS 3.1",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "RAM Type: LPDDR5",
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
                    text = "Hardware Validation",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hardware Consistency",
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