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
 * Screen for displaying network information
 */
@Composable
fun NetworkInfoScreen() {
    val networkInfo = remember {
        mapOf(
            "Network Type" to "WiFi",
            "Connection Status" to "Connected",
            "SSID" to "MyHomeWiFi",
            "BSSID" to "AA:BB:CC:DD:EE:FF",
            "Signal Strength" to "-45 dBm",
            "Link Speed" to "866 Mbps",
            "IP Address" to "192.168.1.100",
            "Subnet Mask" to "255.255.255.0",
            "Gateway" to "192.168.1.1",
            "DNS 1" to "8.8.8.8",
            "DNS 2" to "8.8.4.4",
            "MAC Address" to "AA:BB:CC:DD:EE:FF",
            "Bluetooth" to "Enabled",
            "NFC" to "Available",
            "Cellular" to "Available"
        )
    }
    
    val cellularInfo = remember {
        mapOf(
            "Network Operator" to "Verizon",
            "Network Type" to "5G",
            "Signal Strength" to "-85 dBm",
            "Data Connection" to "LTE",
            "Voice Connection" to "LTE",
            "Roaming" to "No",
            "IMEI" to "358123456789012",
            "IMEI SV" to "1",
            "MEID" to "A100001234567",
            "Device ID" to "358123456789012",
            "Phone Number" to "+1 (555) 123-4567",
            "Network Country" to "US",
            "Network Name" to "Verizon",
            "Network Code" to "311480"
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
                text = "Network Information",
                style = MaterialTheme.typography.titleLarge
            )
            Button(onClick = { /* Refresh network info */ }) {
                Text("Refresh")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // WiFi information
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "WiFi Information",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                networkInfo.forEach { (key, value) ->
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
        
        // Cellular information
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Cellular Information",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                cellularInfo.forEach { (key, value) ->
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
        
        // Bluetooth information
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Bluetooth Information",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "Status: Enabled",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Name: Pixel 7 Pro",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Address: AA:BB:CC:DD:EE:FF",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Class: Phone",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Pairing Mode: Off",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Connected Devices: 0",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
            }
        }
        
        // NFC information
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "NFC Information",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "Status: Available",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "State: Disabled",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Tech List: [NfcA, NfcB, NfcF, NfcV, IsoDep, Ndef]",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Max Transceive Size: 253 bytes",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
                
                Text(
                    text = "Foreground Dispatch: Enabled",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = PaddingValues(bottom = 8.dp)
                )
            }
        }
        
        // Network validation section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Network Validation",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Network Consistency",
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
                        text = "VPN Detection",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Chip(
                        colors = ChipDefaults.chipColors(containerColor = androidx.compose.ui.graphics.Color.Green)
                    ) {
                        Text("No VPN")
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
                        text = "Proxy Detection",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Chip(
                        colors = ChipDefaults.chipColors(containerColor = androidx.compose.ui.graphics.Color.Green)
                    ) {
                        Text("No Proxy")
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