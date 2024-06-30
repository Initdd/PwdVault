package com.example.pwdvault.dal.dto

import kotlinx.serialization.Serializable

@Serializable
data class ThemeModeDT(
    val theme: String
)

fun compareThemeModeDT(themeModeDT1: ThemeModeDT, themeModeDT2: ThemeModeDT): Boolean {
    return themeModeDT1.theme == themeModeDT2.theme
}