package com.example.pwdvault.control

import com.example.pwdvault.dal.Storage
import com.example.pwdvault.dal.domain.ThemeModeDO
import com.example.pwdvault.dal.dto.ThemeModeDT
import com.example.pwdvault.dal.mapper.ThemeModeMapper
import com.example.pwdvault.dal.saveToFile
import java.io.File

/**
 * The ThemeManager class is responsible for managing the theme settings of the application
 * In other words, dark mode or light mode.
 */
class ThemeModeManager(
    private val storage: Storage<Int, ThemeModeDT>,
    private val file: File
) {

    private val defaultTheme = ThemeModeDO.LIGHT

    init {
        // check if the theme is empty
        if (storage.retrieveAll().isEmpty()) {
            storage.store(ThemeModeMapper.toDTO(defaultTheme))
        }
    }

    /**
     * Get the current theme mode.
     */
    fun getTheme(): ThemeModeDO {
        return try {
            ThemeModeMapper.toDomain(storage.retrieveAll().first())
        } catch (e: NoSuchElementException) {
            defaultTheme
        }
    }

    /**
     * Set the theme mode.
     */
    fun setTheme(theme: ThemeModeDO) {
        try{
            storage.update(0, ThemeModeMapper.toDTO(theme))
        } catch (e: IndexOutOfBoundsException) {
            storage.store(ThemeModeMapper.toDTO(theme))
        }
    }

    /**
     * Save to file
     */
    fun saveTMToFile() {
        saveToFile(file, storage.retrieveAll())
    }
}