package com.pennypilot.app.domain.model

enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK
}

data class AppSettings(
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val displayCurrency: String = "MYR"
)
