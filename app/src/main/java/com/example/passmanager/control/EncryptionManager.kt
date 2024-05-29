package com.example.passmanager.control

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptionManager {
    val IVBYTES: ByteArray = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private val selectedEncryption = "AES/CBC/PKCS5Padding"

    fun encrypt(key: String, data: String): String {
        val instance = MessageDigest.getInstance("SHA-256")
        val bytes: ByteArray = key.toByteArray(StandardCharsets.UTF_8)
        instance.update(bytes, 0, bytes.size)
        val secretKeySpec = SecretKeySpec(instance.digest(), "AES")
        val instance2 = Cipher.getInstance(this.selectedEncryption)
        instance2.init(1, secretKeySpec, IvParameterSpec(this.IVBYTES))
        val doFinal = instance2.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(doFinal)
    }

    fun decrypt(key: String, data: String?): String {
        val instance = MessageDigest.getInstance("SHA-256")
        val bytes: ByteArray = key.toByteArray(StandardCharsets.UTF_8)
        instance.update(bytes, 0, bytes.size)
        val secretKeySpec = SecretKeySpec(instance.digest(), "AES")
        val decode = Base64.getDecoder().decode(data)
        val instance2 = Cipher.getInstance(this.selectedEncryption)
        instance2.init(2, secretKeySpec, IvParameterSpec(this.IVBYTES))
        val doFinal = instance2.doFinal(decode)
        return String(doFinal, StandardCharsets.UTF_8)
    }
}