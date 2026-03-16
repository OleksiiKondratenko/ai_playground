package com.aiplayground.mathlib

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NativeMathTest {

    private lateinit var math: NativeMath

    @Before
    fun setUp() {
        math = NativeMath()
    }

    @Test
    fun addPositiveNumbers() {
        assertEquals(5, math.add(2, 3))
    }

    @Test
    fun addWithZero() {
        assertEquals(7, math.add(7, 0))
    }

    @Test
    fun addNegativeNumbers() {
        assertEquals(-5, math.add(-2, -3))
    }

    @Test
    fun multiplyPositiveNumbers() {
        assertEquals(6, math.multiply(2, 3))
    }

    @Test
    fun multiplyWithZero() {
        assertEquals(0, math.multiply(5, 0))
    }

    @Test
    fun multiplyNegativeNumbers() {
        assertEquals(6, math.multiply(-2, -3))
    }

    @Test
    fun multiplyMixedSigns() {
        assertEquals(-6, math.multiply(2, -3))
    }

    @Test
    fun cairoVersionIsNotEmpty() {
        val version = math.cairoVersion()
        assertTrue("Cairo version should not be empty", version.isNotEmpty())
    }
}
