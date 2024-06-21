package com.example.passmanager.control

import com.example.passmanager.dal.StorageJSON
import com.example.passmanager.dal.domain.ThemeModeDO
import com.example.passmanager.dal.dto.ThemeModeDT
import com.example.passmanager.dal.mapper.ThemeModeMapper
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
        file = File.createTempFile("theme", "json")
        storage = StorageJSON { a, b -> a.theme == b.theme }
        themeModeManager = ThemeModeManager(storage, file)
    }

    @Test
    fun retrievesDefaultThemeWhenStorageIsEmpty() {
        val theme = themeModeManager.getTheme()
        assertEquals(ThemeModeDO.LIGHT, theme)
    }

    @Test
    fun retrievesStoredTheme() {
        val darkTheme = ThemeModeDO.DARK
        storage.store(ThemeModeMapper.toDTO(darkTheme))
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

    @Test
    fun savesThemeToFile() {
        val darkTheme = ThemeModeDO.DARK
        themeModeManager.setTheme(darkTheme)
        themeModeManager.saveTMToFile()
        val fileContent = file.readText()
        assertEquals("[{\"theme\":\"DARK\"}]", fileContent)
    }
}