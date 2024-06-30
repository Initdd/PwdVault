package com.example.pwdvault.dal.domain

enum class ThemeModeDO {
    LIGHT,
    DARK
}

fun compareThemeModeDO(tm1: ThemeModeDO, tm2: ThemeModeDO): Boolean {
    return tm1 == tm2
}