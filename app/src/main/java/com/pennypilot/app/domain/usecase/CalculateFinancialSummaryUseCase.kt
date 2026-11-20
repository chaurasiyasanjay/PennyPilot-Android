package com.pennypilot.app.domain.usecase

import com.pennypilot.app.domain.model.FinancialSummary
import com.pennypilot.app.domain.model.Transaction
import com.pennypilot.app.domain.model.TransactionType
import javax.inject.Inject

/**
 * Calculates income, expenses and balance from a collection of transactions.
 *
 * This operation is pure business logic: it has no Android, database or
 * network dependency, so it can be tested with ordinary JVM unit tests.
 */
class CalculateFinancialSummaryUseCase @Inject constructor() {

    operator fun invoke(transactions: List<Transaction>): FinancialSummary {
        val totalIncome = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amountMinor }

        val totalExpense = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amountMinor }

        return FinancialSummary(
            totalIncomeMinor = totalIncome,
            totalExpenseMinor = totalExpense,
            balanceMinor = totalIncome - totalExpense
        )
    }
}
