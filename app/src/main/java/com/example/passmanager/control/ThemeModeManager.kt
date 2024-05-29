package com.example.passmanager.control

import com.example.passmanager.dal.Storage
import com.example.passmanager.dal.domain.ThemeModeDO
import com.example.passmanager.dal.dto.ThemeModeDT
import com.example.passmanager.dal.mapper.ThemeModeMapper

/**
 * The ThemeManager class is responsible for managing the theme settings of the application
 * In other words, dark mode or light mode.
 */
class ThemeModeManager(
    private val storage: Storage<ThemeModeDT>
) {
    /**
     * Get the current theme mode.
     */
    fun getTheme(): ThemeModeDO {
        return ThemeModeMapper.toDomain(storage.retrieveAll().first())
    }

    /**
     * Set the theme mode.
     */
    fun setTheme(theme: ThemeModeDO) {
        storage.store(ThemeModeMapper.toDto(theme))
    }
}