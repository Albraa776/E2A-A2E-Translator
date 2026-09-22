package com.device.spec.extractor.di

import android.content.Context
import com.device.spec.extractor.sensors.AntiSpoofingDetector
import com.device.spec.extractor.sensors.CrossValidationManager
import com.device.spec.extractor.sensors.HardwareInfoManager
import com.device.spec.extractor.sensors.SensorManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module providing sensor-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object SensorsModule {
    
    @Provides
    @Singleton
    fun provideSensorManager(
        @ApplicationContext context: Context
    ): SensorManager {
        return SensorManager(context)
    }
    
    @Provides
    @Singleton
    fun provideHardwareInfoManager(
        @ApplicationContext context: Context
    ): HardwareInfoManager {
        return HardwareInfoManager(context)
    }
    
    @Provides
    @Singleton
    fun provideAntiSpoofingDetector(
        @ApplicationContext context: Context
    ): AntiSpoofingDetector {
        return AntiSpoofingDetector(context)
    }
    
    @Provides
    @Singleton
    fun provideCrossValidationManager(
        @ApplicationContext context: Context
    ): CrossValidationManager {
        return CrossValidationManager(context)
    }
}