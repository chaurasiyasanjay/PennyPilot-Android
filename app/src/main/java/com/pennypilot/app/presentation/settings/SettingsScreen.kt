package com.pennypilot.app.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pennypilot.app.domain.model.ThemePreference

private val supportedCurrencies = listOf(
    "MYR" to "Malaysian Ringgit",
    "USD" to "US Dollar",
    "EUR" to "Euro",
    "GBP" to "British Pound",
    "INR" to "Indian Rupee",
    "SGD" to "Singapore Dollar"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    var currencyMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Appearance", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Choose how PennyPilot looks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ThemePreference.entries.forEach { preference ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = settings.themePreference == preference,
                            onClick = { viewModel.onThemeSelected(preference) }
                        )
                        Text(
                            text = when (preference) {
                                ThemePreference.SYSTEM -> "System default"
                                ThemePreference.LIGHT -> "Light"
                                ThemePreference.DARK -> "Dark"
                            }
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Display currency", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Choose your preferred currency",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                androidx.compose.foundation.layout.Box {
                    OutlinedButton(onClick = { currencyMenuExpanded = true }) {
                        Text(
                            supportedCurrencies.firstOrNull { it.first == settings.displayCurrency }
                                ?.let { "${it.first} — ${it.second}" }
                                ?: settings.displayCurrency
                        )
                    }
                    DropdownMenu(
                        expanded = currencyMenuExpanded,
                        onDismissRequest = { currencyMenuExpanded = false }
                    ) {
                        supportedCurrencies.forEach { (code, name) ->
                            DropdownMenuItem(
                                text = { Text("$code — $name") },
                                onClick = {
                                    viewModel.onCurrencySelected(code)
                                    currencyMenuExpanded = false
                                }
                            )
                        }
                    }
                }
                Text(
                    "Currency conversion will be applied when exchange-rate integration is added. Transactions are currently stored in MYR.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
