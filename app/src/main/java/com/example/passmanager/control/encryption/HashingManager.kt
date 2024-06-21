package com.example.passmanager.control.encryption

import java.security.MessageDigest

object HashingManager {
    private fun hashBytes(input: ByteArray): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(input)
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }
    fun hashString(input: String): String {
        if (input.isEmpty()) {
            throw IllegalArgumentException("Input string must not be empty")
        }
        return hashBytes(input.toByteArray())
    }

    fun hashInt(input: Int): String {
        return hashBytes(input.toString().toByteArray())
    }

}