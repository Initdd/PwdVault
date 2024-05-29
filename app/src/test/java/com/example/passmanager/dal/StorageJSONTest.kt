package com.example.passmanager.dal

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
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
    fun `store adds new unique data successfully`() {
        val storage = StorageJSON<CustomObject>(file) { a, b -> a.id == b.id }
        val result = storage.store(CustomObject(1, "testData"))
        assertTrue(result)
    }

    @Test
    fun `store does not add duplicate data`() {
        val storage = StorageJSON<CustomObject>(file, { a, b -> a.id == b.id })
        storage.store(CustomObject(1, "testData"))
        val result = storage.store(CustomObject(1, "testData"))
        assertFalse(result)
    }

    @Test
    fun `retrieve returns correct data when key is provided`() {
        val storage = StorageJSON<CustomObject>(file, { a, b -> a.id == b.id })
        storage.store(CustomObject(1, "testData"))
        val result = storage.retrieve(CustomObject(1, ""))
        assertEquals(CustomObject(1, "testData"), result)
    }

    @Test
    fun `retrieve returns null when key is not found`() {
        val storage = StorageJSON<CustomObject>(file, { a, b -> a.id == b.id })
        val result = storage.retrieve(CustomObject(1, ""))
        assertNull(result)
    }

    @Test
    fun `delete removes data successfully`() {
        val storage = StorageJSON<CustomObject>(file, { a, b -> a.id == b.id })
        storage.store(CustomObject(1, "testData"))
        val result = storage.delete(CustomObject(1, ""))
        assertTrue(result)
    }

    @Test
    fun `delete returns false when data is not found`() {
        val storage = StorageJSON<CustomObject>(file, { a, b -> a.id == b.id })
        val result = storage.delete(CustomObject(1, ""))
        assertFalse(result)
    }

    @Test
    fun `update successfully updates existing data`() {
        val storage = StorageJSON<CustomObject>(file, { a, b -> a.id == b.id })
        storage.store(CustomObject(1, "oldData"))
        val result = storage.update(CustomObject(1, "newData"))
        assertTrue(result)
    }

    @Test
    fun `update returns false when data is not found`() {
        val storage = StorageJSON<CustomObject>(file, { a, b -> a.id == b.id })
        val result = storage.update(CustomObject(1, "newData"))
        assertFalse(result)
    }

    @Test
    fun `deleteAll removes all data`() {
        val storage = StorageJSON<CustomObject>(file, { a, b -> a.id == b.id })
        storage.store(CustomObject(1, "testData"))
        storage.store(CustomObject(2, "testData"))
        storage.deleteAll()
        val result = storage.retrieveAll()
        assertEquals(0, result.size)
    }

    @Test
    fun `retrieveAll returns all data`() {
        val storage = StorageJSON<CustomObject>(file, { a, b -> a.id == b.id })
        storage.store(CustomObject(1, "testData"))
        storage.store(CustomObject(2, "testData"))
        val result = storage.retrieveAll()
        assertEquals(2, result.size)
    }

    @Test
    fun `store adds new data successfully`() {
        val storage = StorageJSON<CustomObject>(file, { a, b -> a.id == b.id })
        val result = storage.store(CustomObject(1, "testData"))
        saveToFile(file, listOf(CustomObject(1, "testData")))
        assertTrue(result)
    }

    @Test
    fun `loadFromFile loads data from file`() {
        val storage = StorageJSON<CustomObject>(file, { a, b -> a.id == b.id })
        saveToFile(file, listOf(CustomObject(1, "testData")))
        val result = loadFromFile<CustomObject>(file)
        assertEquals(1, result.size)
    }


}