package com.example.passmanager.control.encryption

import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class HashingManagerTest {

    @Test
    fun hashString_shouldReturnExpectedHash_whenInputIsAlphanumeric() {
        val input = "abc123"
        val actual = HashingManager.hashString(input)
        assertTrue(actual.isNotEmpty())
    }

    @Test
    fun hashString_shouldReturnExpectedHash_whenInputIsEmpty() {
        val input = ""
        assertThrows(IllegalArgumentException::class.java) {
            HashingManager.hashString(input)
        }
    }

    @Test
    fun hashInt_shouldReturnExpectedHash_whenInputIsPositive() {
        val input = 123
        val actual = HashingManager.hashInt(input)
        assertTrue(actual.isNotEmpty())
    }

    @Test
    fun hashInt_shouldReturnExpectedHash_whenInputIsNegative() {
        val input = -123
        val actual = HashingManager.hashInt(input)
        assertTrue(actual.isNotEmpty())
    }

    @Test
    fun hashInt_shouldReturnExpectedHash_whenInputIsZero() {
        val input = 0
        val actual = HashingManager.hashInt(input)
        assertTrue(actual.isNotEmpty())
    }
}