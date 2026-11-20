package com.pennypilot.app.presentation.dashboard

import com.pennypilot.app.domain.model.Category
import com.pennypilot.app.domain.model.FinancialSummary
import com.pennypilot.app.domain.model.Transaction

/**
 * State rendered by the dashboard screen.
 * Transaction and summary amounts remain stored/calculated in MYR sen;
 * [exchangeRate] is applied only when formatting values for display.
 */
data class DashboardUiState(
    val isLoading: Boolean = true,
    val transactions: List<Transaction> = emptyList(),
    val visibleTransactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Long? = null,
    val categoryNames: Map<Long, String> = emptyMap(),
    val financialSummary: FinancialSummary = FinancialSummary(
        totalIncomeMinor = 0,
        totalExpenseMinor = 0,
        balanceMinor = 0
    ),
    val displayCurrency: String = "MYR",
    val exchangeRate: Double? = 1.0,
    val exchangeRateDate: String? = null,
    val isExchangeRateLoading: Boolean = false,
    val exchangeRateErrorMessage: String? = null,
    val errorMessage: String? = null
)
