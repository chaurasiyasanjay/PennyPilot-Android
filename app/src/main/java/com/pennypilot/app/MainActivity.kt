package com.pennypilot.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pennypilot.app.domain.model.ThemePreference
import com.pennypilot.app.presentation.navigation.PennyPilotNavHost
import com.pennypilot.app.presentation.settings.SettingsViewModel
import com.pennypilot.app.ui.theme.PennyPilotTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PennyPilotAppRoot()
        }
    }
}

@Composable
private fun PennyPilotAppRoot(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settings = settingsViewModel.settings.collectAsStateWithLifecycle().value

    PennyPilotTheme(themePreference = settings.themePreference) {
        PennyPilotNavHost()
    }
}
