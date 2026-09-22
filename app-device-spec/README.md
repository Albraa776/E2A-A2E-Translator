# Device Spec Extractor

An Android app that extracts real device hardware specifications even if the OS is faked. This app uses multiple sources including sensors, hardware information, and system data to provide accurate device identification and anti-spoofing detection.

## Features

### 🎯 Core Functionality
- **Real Device Spec Extraction**: Collects actual hardware specifications from multiple sources
- **Anti-Spoofing Detection**: Detects if the device is an emulator, rooted, or has tampered with hardware information
- **Cross-Validation**: Validates data consistency across multiple information sources
- **Sensor Data Collection**: Collects and validates data from all available sensors
- **Hardware Information**: Extracts detailed hardware information including CPU, memory, storage, and more

### 🔍 Detection Capabilities
- **Emulator Detection**: Detects running in emulators (Android Studio, Genymotion, etc.)
- **Root Detection**: Identifies if the device is rooted or has root indicators
- **Build Spoofing**: Detects tampered build information and fingerprints
- **Sensor Spoofing**: Identifies fake or manipulated sensor data
- **Network Spoofing**: Detects VPN, proxy, and other network manipulation
- **Location Spoofing**: Identifies mock location and location tampering

### 📊 Data Collection
- **Sensors**: Accelerometer, Gyroscope, Magnetometer, Pressure, Light, Proximity, Step Counter, Step Detector
- **Hardware**: Manufacturer, Model, Brand, Device, Product, Hardware, Board, Bootloader, Fingerprint
- **System**: Android Version, API Level, Build Information, Display Information
- **Network**: WiFi, Cellular, Bluetooth, NFC information
- **Runtime**: Memory, Storage, CPU information

## Architecture

### Core Components

1. **SensorManager**: Handles sensor data collection and validation
2. **HardwareInfoManager**: Collects and validates hardware information
3. **AntiSpoofingDetector**: Performs comprehensive anti-spoofing analysis
4. **CrossValidationManager**: Validates data consistency across multiple sources
5. **SpecExtractionService**: Foreground service for continuous monitoring

### Data Flow

```
User Request → SensorManager → HardwareInfoManager → AntiSpoofingDetector → CrossValidationManager → Results
```

## Build Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 17 or later
- Android SDK 35
- Android NDK (optional for native compilation)

### Build Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/Albraa776/Device-Spec-Extractor.git
   cd Device-Spec-Extractor
   ```

2. Open the project in Android Studio:
   ```bash
   android-studio app-device-spec/
   ```

3. Build the APK:
   ```bash
   ./gradlew assembleDebug
   ```

4. Or build release APK:
   ```bash
   ./gradlew assembleRelease
   ```

### GitHub Actions Build

The project includes a GitHub Actions workflow for automated building:

1. Push changes to the `master` or `main` branch
2. The workflow will automatically build and publish the APK
3. The APK will be available as a GitHub release artifact

## Usage

### Basic Usage

1. Install the APK on your Android device
2. Open the app
3. Tap "Extract Specifications" to collect device information
4. View the results in different categories:
   - **Sensor Data**: View sensor readings and validation
   - **Hardware Info**: View hardware specifications
   - **System Info**: View system and build information
   - **Network Info**: View network connectivity information

### Advanced Usage

#### Continuous Monitoring

Start the foreground service for continuous monitoring:

```kotlin
SpecExtractionService.startService(context)
```

Stop the service:

```kotlin
SpecExtractionService.stopService(context)
```

#### Programmatic Access

Get device specifications programmatically:

```kotlin
// Get sensor manager
val sensorManager = SensorManager(context)

// Start collecting sensor data
sensorManager.startCollection()

// Get sensor data
val sensorData = sensorManager.getAllSensorReadings()

// Validate sensor data
val validationResult = sensorManager.validateSensorData()

// Detect spoofing
val spoofingResult = AntiSpoofingDetector(context).detectSensorSpoofing(sensorData)
```

## Permissions

The app requires the following permissions:

### Required Permissions
- `ACCESS_FINE_LOCATION`
- `ACCESS_COARSE_LOCATION`
- `READ_PHONE_STATE`
- `BLUETOOTH`
- `BLUETOOTH_ADMIN`
- `NFC`

### Optional Permissions
- `CAMERA`
- `RECORD_AUDIO`
- `VIBRATE`
- `READ_EXTERNAL_STORAGE`
- `WRITE_EXTERNAL_STORAGE`

## Security Considerations

### Data Protection
- All collected data is stored locally on the device
- No data is sent to external servers
- Sensitive information is handled with care

### Anti-Spoofing Features
- Multiple detection mechanisms for various types of spoofing
- Cross-validation of data from different sources
- Continuous monitoring for runtime anomalies

### Privacy
- No user tracking or analytics
- No collection of personal information
- All data processing happens locally

## Testing

### Unit Testing
Run unit tests:
```bash
./gradlew test
```

### Instrumentation Testing
Run instrumentation tests:
```bash
./gradlew connectedAndroidTest
```

### Manual Testing
Test on various devices including:
- Real Android phones
- Emulators (Android Studio, Genymotion)
- Rooted devices
- Devices with custom ROMs

## Troubleshooting

### Common Issues

1. **Permission Denied**
   - Ensure all required permissions are granted
   - Check if the app is targeting the correct API level

2. **Sensor Not Available**
   - Some sensors may not be available on all devices
   - Check device specifications for supported sensors

3. **Build Errors**
   - Ensure JDK 17 is installed
   - Check Android SDK and NDK compatibility

4. **Performance Issues**
   - Close background apps to free up resources
   - Restart the device if experiencing lag

### Debug Mode

Enable debug logging:
```kotlin
Timber.plant(Timber.DebugTree())
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Android Open Source Project
- Hilt for dependency injection
- Timber for logging
- Kotlin Coroutines for asynchronous programming

## Support

For support, please open an issue on GitHub or contact the maintainer.

---

**Note**: This app is designed for security research and device identification purposes. Please use it responsibly and in compliance with applicable laws and regulations.