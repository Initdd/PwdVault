package com.example.passmanager.dal

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import kotlinx.serialization.Serializable
import org.junit.Test
import java.io.File

class StorageJSONTest {

    companion object {
        @Serializable
        data class CustomObject(val id: Int, val data: String)

        val file: File = kotlin.io.path.createTempFile("test", "json").toFile()
    }

    @Test
    fun `load adds data to storage`() {
        val storage = StorageJSON<CustomObject>{ a, b -> a.id == b.id }
        val data = listOf(CustomObject(1, "testData"), CustomObject(2, "testData2"))
        storage.load(data)
        val result = storage.retrieveAll()
        assertEquals(2, result.size)
    }

    @Test
    fun `load replaces existing data in storage`() {
        val storage = StorageJSON<CustomObject>{ a, b -> a.id == b.id }
        storage.store(CustomObject(1, "oldData"))
        val data = listOf(CustomObject(1, "testData"), CustomObject(2, "testData2"))
        storage.load(data)
        val result = storage.retrieveAll()
        assertEquals(2, result.size)
        assertEquals("testData", result[0].data)
        assertEquals("testData2", result[1].data)
    }

    @Test
    fun `retrieve returns null when index is out of bounds`() {
        val storage = StorageJSON<CustomObject>{ a, b -> a.id == b.id }
        val result = storage.retrieve(1)
        assertNull(result)
    }

    @Test
    fun `delete returns false when index is out of bounds`() {
        val storage = StorageJSON<CustomObject>{ a, b -> a.id == b.id }
        val result = storage.delete(1)
        assertFalse(result)
    }

    @Test
    fun `update returns false when index is out of bounds`() {
        val storage = StorageJSON<CustomObject>{ a, b -> a.id == b.id }
        val result = storage.update(1, CustomObject(1, "newData"))
        assertFalse(result)
    }

}