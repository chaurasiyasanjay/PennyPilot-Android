package com.pennypilot.app.domain.repository

import com.pennypilot.app.domain.model.AppSettings
import com.pennypilot.app.domain.model.ThemePreference
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun setThemePreference(themePreference: ThemePreference)
    suspend fun setDisplayCurrency(currencyCode: String)
}
