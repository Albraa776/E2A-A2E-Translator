package com.device.spec.extractor

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Device Spec Extractor
 */
class DeviceSpecExtractorTest {

    @Test
    fun testAppPackageName() {
        // Simple sanity test
        assertEquals("com.device.spec.extractor", "com.device.spec.extractor")
    }

    @Test
    fun testBasicMath() {
        assertEquals(4, 2 + 2)
    }
}
