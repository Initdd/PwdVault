package com.example.pwdvault.dal

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * Storage implementation using SharedPreferences.
 *
 * @param context The application context.
 * @param fileName The name of the SharedPreferences file.
 * @param serializer The serializer for the list of elements.
 * @param compare A function to compare two elements of type E.
 */
class StorageSP<E>(
    context: Context,
    fileName: String,
    private val serializer: KSerializer<List<E>>,
    private val compare: (E, E) -> Boolean
) : Storage<Int, E> {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    private var storage: MutableList<E> = mutableListOf()
    private val key = "storage_data"

    init {
        loadFromPrefs()
    }

    private fun loadFromPrefs() {
        val content = sharedPreferences.getString(key, null)
        if (content != null) {
            try {
                storage = Json.decodeFromString(serializer, content).toMutableList()
            } catch (e: Exception) {
                storage = mutableListOf()
            }
        }
    }

    private fun saveToPrefs() {
        val content = Json.encodeToString(serializer, storage.toList())
        sharedPreferences.edit().putString(key, content).apply()
    }

    override fun store(data: E): Boolean {
        if (storage.any { compare(it, data) }) {
            return false
        }
        storage.add(data)
        saveToPrefs()
        return true
    }

    override fun retrieve(key: Int): E? = try {
        storage[key]
    } catch (e: IndexOutOfBoundsException) {
        null
    }

    override fun retrieveAll(): List<E> {
        return storage
    }

    override fun delete(key: Int): Boolean {
        if (key in storage.indices) {
            storage.removeAt(key)
            saveToPrefs()
            return true
        }
        return false
    }

    override fun deleteAll() {
        storage.clear()
        saveToPrefs()
    }

    override fun update(key: Int, data: E): Boolean {
        if (key in storage.indices) {
            storage[key] = data
            saveToPrefs()
            return true
        }
        return false
    }

    /**
     * Load data into the storage list and persist it.
     * Useful for migration or importing.
     */
    fun load(data: List<E>) {
        storage = data.toMutableList()
        saveToPrefs()
    }
}
