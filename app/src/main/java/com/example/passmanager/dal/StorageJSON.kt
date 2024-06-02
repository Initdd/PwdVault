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
}

class StorageJSON<E>(
    private val compare: (E, E) -> Boolean
) : Storage<E> {
    var storage: MutableList<E> = mutableListOf()

    override fun store(data: E): Boolean {
        if (storage.any { compare(it, data) }) {
            return false
        }
        storage.add(data)
        return true
    }

    @Suppress("UNCHECKED_CAST")
    override fun retrieve(key: E?): E? {
        // If the key is null, return all the data
        if (key == null) {
            return storage as E
        }
        return storage.find {
            compare(it, key)
        } as E
    }

    override fun retrieveAll(): List<E> {
        return storage
    }

    override fun delete(key: E): Boolean {
        val item = storage.find {
            compare(it, key)
        }
        if (item != null) {
            storage.remove(item)
            return true
        }
        return false
    }

    override fun deleteAll() {
        storage.clear()
    }

    override fun update(key: E): Boolean {
        val item = storage.find {
            compare(it, key)
        }
        if (item != null) {
            storage.remove(item)
            storage.add(key)
            return true
        }
        return false
    }

    fun load(data: List<E>) {
        storage = data.toMutableList()
    }

}