package com.example.passmanager.dal

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*
import java.io.File


inline fun <reified T> loadFromFile(file: File): List<T> {
    val content = file.readText()
    return try {
        Json.decodeFromString<List<T>>(content)
    } catch (e: SerializationException) {
        emptyList()
    }
}

inline fun <reified T> saveToFile(file: File, data: List<T>) {
    val content = Json.encodeToString<List<T>>(data)
    file.writeText(content)
    println("Data saved to file:\n$content")
}

/**
 * Storage Interface
 *
 * This interface defines the methods that a storage class must implement.
 * The storage class is responsible for storing and retrieving data from the storage medium.
 *
 */
class StorageJSON<E>(
    private val compare: (E, E) -> Boolean
) : Storage<Int, E> {
    var storage: MutableList<E> = mutableListOf()

    /**
     * Store data in the storage list.
     *
     * @param data The data to be stored.
     * @return True if the data was successfully stored, false otherwise.
     * If the data is already stored, return false.
     */
    override fun store(data: E): Boolean {
        if (storage.any { compare(it, data) }) {
            return false
        }
        storage.add(data)
        return true
    }

    /**
     * Retrieve data from the storage list.
     *
     * @param key The key to search for in the storage list.
     * @return The data stored under the given key, or null if the key does not exist.
     */
    override fun retrieve(key: Int): E? = storage[key]

    /**
     * Retrieve all data from the storage list.
     *
     * @return A list containing all the data stored in the storage list.
     */
    override fun retrieveAll(): List<E> {
        return storage
    }

    /**
     * Delete data from the storage list.
     *
     * @param key The key to search for in the storage list.
     * @return True if the data was successfully deleted, false otherwise.
     */
    override fun delete(key: Int): Boolean {
        val item = storage[key]
        if (item != null) {
            storage.remove(item)
            return true
        }
        return false
    }

    /**
     * Delete all data from the storage list.
     */
    override fun deleteAll() {
        storage.clear()
    }

    /**
     * Update data in the storage list.
     *
     * @param key The key to search for in the storage list.
     * @param data The new data to be stored.
     * @return True if the data was successfully updated, false otherwise.
     */
    override fun update(key: Int, data: E): Boolean {
        if (storage[key] != null) {
            storage[key] = data
            return true
        }
        return false
    }

    /**
     * Load data into the storage list.
     *
     * @param data The data to load into the storage list.
     */
    fun load(data: List<E>) {
        storage = data.toMutableList()
    }

}