package com.pennypilot.app.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pennypilot.app.domain.model.AppSettings
import com.pennypilot.app.domain.model.ThemePreference
import com.pennypilot.app.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "penny_pilot_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private object PreferencesKeys {
        val THEME = stringPreferencesKey("theme_preference")
        val DISPLAY_CURRENCY = stringPreferencesKey("display_currency")
    }

    override fun observeSettings(): Flow<AppSettings> =
        context.settingsDataStore.data.map { preferences ->
            AppSettings(
                themePreference = preferences[PreferencesKeys.THEME]
                    ?.let { stored -> ThemePreference.entries.firstOrNull { it.name == stored } }
                    ?: ThemePreference.SYSTEM,
                displayCurrency = preferences[PreferencesKeys.DISPLAY_CURRENCY] ?: "MYR"
            )
        }

    override suspend fun setThemePreference(themePreference: ThemePreference) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = themePreference.name
        }
    }

    override suspend fun setDisplayCurrency(currencyCode: String) {
        require(currencyCode in supportedCurrencies) { "Unsupported currency code" }
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.DISPLAY_CURRENCY] = currencyCode
        }
    }

    private companion object {
        val supportedCurrencies = setOf("MYR", "USD", "EUR", "GBP", "INR", "SGD")
    }
}
