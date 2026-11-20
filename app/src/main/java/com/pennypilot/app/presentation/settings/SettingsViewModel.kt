package com.pennypilot.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pennypilot.app.domain.model.AppSettings
import com.pennypilot.app.domain.model.ThemePreference
import com.pennypilot.app.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.observeSettings()
        .catch { exception ->
            LOGGER.severe("Unable to observe settings; using defaults" + ": " + exception.message)
            emit(AppSettings())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings()
        )

    private companion object {
        val LOGGER: java.util.logging.Logger = java.util.logging.Logger.getLogger("PennyPilotSettings")
    }

    fun onThemeSelected(themePreference: ThemePreference) {
        viewModelScope.launch {
            try {
                settingsRepository.setThemePreference(themePreference)
            } catch (exception: Exception) {
                LOGGER.severe("Unable to save theme preference" + ": " + exception.message)
            }
        }
    }

    fun onCurrencySelected(currencyCode: String) {
        viewModelScope.launch {
            try {
                settingsRepository.setDisplayCurrency(currencyCode)
            } catch (exception: Exception) {
                LOGGER.severe("Unable to save display currency" + ": " + exception.message)
            }
        }
    }
}
