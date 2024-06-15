package com.example.passmanager.control.encryption

import org.junit.Assert.assertEquals
import org.junit.Test

class HashingManagerTest {

    @Test
    fun testHashString() {
        val input = "password"
        val expected = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"
        val actual = HashingManager.hashString(input)
        assertEquals(expected, actual)
    }

    @Test
    fun testGetHashOfString() {
        val input = "1"
        println(HashingManager.hashString(input))
    }
}