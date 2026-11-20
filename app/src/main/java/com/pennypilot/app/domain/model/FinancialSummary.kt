package com.pennypilot.app.domain.model

/**
 * Financial totals calculated from the user's transactions.
 *
 * All values use the base currency's minor unit (MYR sen).
 */
data class FinancialSummary(
    val totalIncomeMinor: Long,
    val totalExpenseMinor: Long,
    val balanceMinor: Long
)
