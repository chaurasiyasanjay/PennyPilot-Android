package com.pennypilot.app.presentation.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pennypilot.app.domain.model.Transaction
import com.pennypilot.app.domain.model.TransactionType
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onAddTransaction: () -> Unit,
    onEditTransaction: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransaction
            ) {
                Text(
                    text = "+ Add",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PennyPilot",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    TextButton(onClick = onOpenSettings) {
                        Text("Settings")
                    }
                }
            )
        }
    ) { contentPadding ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Loading your finances…")
                }
            }

            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "We couldn't load your transactions",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Your overview",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "A simple view of your money",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            when {
                                uiState.isExchangeRateLoading -> Text("Updating ${uiState.displayCurrency} exchange rate…", style = MaterialTheme.typography.bodySmall)
                                uiState.exchangeRateErrorMessage != null -> Text(uiState.exchangeRateErrorMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                                uiState.displayCurrency != "MYR" && uiState.exchangeRate != null -> Text(
                                    text = "1 MYR = ${formatExchangeRate(uiState.exchangeRate)} ${uiState.displayCurrency}" +
                                        (uiState.exchangeRateDate?.let { " · Rate date: $it" } ?: ""),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        SummaryCard(
                            title = "Available balance",
                            amountMinor = uiState.financialSummary.balanceMinor,
                            currencyCode = uiState.displayCurrency,
                            exchangeRate = uiState.exchangeRate ?: 1.0,
                            emphasize = true
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SummaryCard(
                                title = "Income",
                                amountMinor = uiState.financialSummary.totalIncomeMinor,
                                currencyCode = uiState.displayCurrency,
                                exchangeRate = uiState.exchangeRate ?: 1.0,
                                modifier = Modifier.weight(1f)
                            )
                            SummaryCard(
                                title = "Expenses",
                                amountMinor = uiState.financialSummary.totalExpenseMinor,
                                currencyCode = uiState.displayCurrency,
                                exchangeRate = uiState.exchangeRate ?: 1.0,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Filter by category",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = uiState.selectedCategoryId == null,
                                onClick = { viewModel.onCategorySelected(null) },
                                label = { Text("All") }
                            )
                            uiState.categories.forEach { category ->
                                FilterChip(
                                    selected = uiState.selectedCategoryId == category.id,
                                    onClick = { viewModel.onCategorySelected(category.id) },
                                    label = { Text(category.name) }
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Recent transactions",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (uiState.visibleTransactions.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (uiState.transactions.isEmpty()) "No transactions yet" else "No transactions in this category",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (uiState.transactions.isEmpty()) "Your income and expenses will appear here." else "Try selecting another category or All.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(
                            items = uiState.visibleTransactions,
                            key = { transaction -> transaction.id }
                        ) { transaction ->
                            TransactionRow(
                                transaction = transaction,
                                categoryName = uiState.categoryNames[transaction.categoryId],
                                currencyCode = uiState.displayCurrency,
                                exchangeRate = uiState.exchangeRate ?: 1.0,
                                onClick = { onEditTransaction(transaction.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amountMinor: Long,
    currencyCode: String,
    exchangeRate: Double,
    modifier: Modifier = Modifier,
    emphasize: Boolean = false
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (emphasize) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatCurrency(amountMinor, currencyCode, exchangeRate),
                style = if (emphasize) {
                    MaterialTheme.typography.headlineSmall
                } else {
                    MaterialTheme.typography.titleMedium
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: Transaction,
    categoryName: String?,
    currencyCode: String,
    exchangeRate: Double,
    onClick: () -> Unit
) {
    val isIncome = transaction.type == TransactionType.INCOME

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = transaction.note.ifBlank {
                        if (isIncome) "Income" else "Expense"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = categoryName ?: "Unknown category",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = (if (isIncome) "+" else "−") + formatCurrency(transaction.amountMinor, currencyCode, exchangeRate),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isIncome) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

private fun formatExchangeRate(rate: Double): String {
    return BigDecimal.valueOf(rate)
        .setScale(4, java.math.RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()
}

private fun formatCurrency(amountMinor: Long, currencyCode: String, exchangeRate: Double): String {
    val convertedAmount = BigDecimal.valueOf(amountMinor, 2)
        .multiply(BigDecimal.valueOf(exchangeRate))
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "MY")).apply {
        currency = Currency.getInstance(currencyCode)
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return formatter.format(convertedAmount)
}
