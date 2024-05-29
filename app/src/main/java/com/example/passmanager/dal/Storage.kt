package com.example.passmanager.dal

/**
 * Storage Interface
 *
 * This interface defines the methods that a storage class must implement.
 * The storage class is responsible for storing and retrieving data from the storage medium.
 *
 *
 */
interface Storage<E> {
    /**
     * Store data in the storage medium.
     *
     * @param key The key under which the data will be stored.
     * @param data The data to be stored.
     * @return True if the data was successfully stored, false otherwise.
     */
    fun store(data: E): Boolean

    /**
     * Retrieve data from the storage medium.
     *
     * The data is retrieved based on the key provided.
     * If the key does not exist, null is returned.
     * If the key is null, the method should return all the data stored in the storage.
     *
     * @param key The key under which the data is stored.
     * @return The data stored under the given key, or null if the key does not exist.
     */
    fun retrieve(key: E?): E?

    /**
     * Get all data from the storage medium.
     *
     * This method retrieves all the data stored in the storage medium.
     *
     * @return A list containing all the data stored in the storage medium.
     */
    fun retrieveAll(): List<E>

    /**
     * Delete data from the storage medium.
     *
     * The data is deleted based on the key provided.
     * If the key does not exist, false is returned.
     *
     * @param key The key under which the data is stored.
     * @return True if the data was successfully deleted, false otherwise.
     */
    fun delete(key: E): Boolean

    /**
     * Delete all data from the storage medium.
     *
     * This method deletes all the data stored in the storage medium.
     */
    fun deleteAll()

    /**
     * Update data in the storage medium.
     *
     * The data is updated based on the key provided.
     * If the key does not exist, false is returned.
     *
     * @param key The key under which the data is stored.
     * @param data The data to be updated.
     * @return True if the data was successfully updated, false otherwise.
     */
    fun update(key: E): Boolean

}