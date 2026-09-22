package com.device.spec.extractor

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/**
 * Entry point for Hilt dependency injection
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface HiltEntryPoint {
    @ApplicationContext
    fun context(): Context
}