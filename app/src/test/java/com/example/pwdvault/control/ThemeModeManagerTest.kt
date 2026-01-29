package com.example.pwdvault.control

import com.example.pwdvault.dal.StorageJSON
import com.example.pwdvault.dal.domain.ThemeModeDO
import com.example.pwdvault.dal.dto.ThemeModeDT
import com.example.pwdvault.dal.mapper.ThemeModeMapper
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

class ThemeModeManagerTest {

    private lateinit var themeModeManager: ThemeModeManager
    private lateinit var storage: StorageJSON<ThemeModeDT>
    private lateinit var file: File

    @Before
    fun setup() {
        storage = StorageJSON { a, b -> a.theme == b.theme }
        themeModeManager = ThemeModeManager(storage)
    }

    @Test
    fun retrievesDefaultThemeWhenStorageIsEmpty() {
        val theme = themeModeManager.getTheme()
        assertEquals(ThemeModeDO.LIGHT, theme)
    }

    @Test
    fun retrievesStoredTheme() {
        val darkTheme = ThemeModeDO.DARK
        themeModeManager.setTheme(darkTheme)
        val theme = themeModeManager.getTheme()
        assertEquals(darkTheme, theme)
    }

    @Test
    fun updatesExistingTheme() {
        val lightTheme = ThemeModeDO.LIGHT
        storage.store(ThemeModeMapper.toDTO(lightTheme))
        val darkTheme = ThemeModeDO.DARK
        themeModeManager.setTheme(darkTheme)
        val theme = themeModeManager.getTheme()
        assertEquals(darkTheme, theme)
    }

    @Test
    fun storesThemeWhenNoneExists() {
        val darkTheme = ThemeModeDO.DARK
        themeModeManager.setTheme(darkTheme)
        val theme = themeModeManager.getTheme()
        assertEquals(darkTheme, theme)
    }
}