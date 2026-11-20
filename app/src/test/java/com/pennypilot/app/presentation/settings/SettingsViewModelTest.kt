package com.pennypilot.app.presentation.settings

import com.pennypilot.app.domain.model.AppSettings
import com.pennypilot.app.domain.model.ThemePreference
import com.pennypilot.app.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeSettingsRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeSettingsRepository()
        viewModel = SettingsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun defaultSettingsUseSystemThemeAndMyr() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.settings.collect() }
        advanceUntilIdle()

        assertEquals(ThemePreference.SYSTEM, viewModel.settings.value.themePreference)
        assertEquals("MYR", viewModel.settings.value.displayCurrency)
    }

    @Test
    fun selectingThemePersistsAndUpdatesObservedSettings() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.settings.collect() }
        viewModel.onThemeSelected(ThemePreference.DARK)
        advanceUntilIdle()

        assertEquals(ThemePreference.DARK, viewModel.settings.value.themePreference)
        assertEquals(ThemePreference.DARK, repository.currentSettings.value.themePreference)
    }

    @Test
    fun selectingCurrencyPersistsAndUpdatesObservedSettings() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.settings.collect() }
        viewModel.onCurrencySelected("SGD")
        advanceUntilIdle()

        assertEquals("SGD", viewModel.settings.value.displayCurrency)
        assertEquals("SGD", repository.currentSettings.value.displayCurrency)
    }

    private class FakeSettingsRepository : SettingsRepository {
        val currentSettings = MutableStateFlow(AppSettings())

        override fun observeSettings(): Flow<AppSettings> = currentSettings

        override suspend fun setThemePreference(themePreference: ThemePreference) {
            currentSettings.value = currentSettings.value.copy(themePreference = themePreference)
        }

        override suspend fun setDisplayCurrency(currencyCode: String) {
            currentSettings.value = currentSettings.value.copy(displayCurrency = currencyCode)
        }
    }
}
