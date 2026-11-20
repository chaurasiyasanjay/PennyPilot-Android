package com.pennypilot.app.domain.usecase

import com.pennypilot.app.domain.model.Transaction
import com.pennypilot.app.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateFinancialSummaryUseCaseTest {

    private val calculateFinancialSummary = CalculateFinancialSummaryUseCase()

    @Test
    fun emptyTransactionsProduceZeroTotals() {
        val result = calculateFinancialSummary(emptyList())

        assertEquals(0L, result.totalIncomeMinor)
        assertEquals(0L, result.totalExpenseMinor)
        assertEquals(0L, result.balanceMinor)
    }

    @Test
    fun mixedTransactionsCalculateIncomeExpenseAndBalance() {
        val transactions = listOf(
            transaction(amountMinor = 500_000, type = TransactionType.INCOME),
            transaction(amountMinor = 125_000, type = TransactionType.EXPENSE),
            transaction(amountMinor = 75_000, type = TransactionType.EXPENSE)
        )

        val result = calculateFinancialSummary(transactions)

        assertEquals(500_000L, result.totalIncomeMinor)
        assertEquals(200_000L, result.totalExpenseMinor)
        assertEquals(300_000L, result.balanceMinor)
    }

    @Test
    fun expensesGreaterThanIncomeProduceNegativeBalance() {
        val transactions = listOf(
            transaction(amountMinor = 100_000, type = TransactionType.INCOME),
            transaction(amountMinor = 150_000, type = TransactionType.EXPENSE)
        )

        val result = calculateFinancialSummary(transactions)

        assertEquals(100_000L, result.totalIncomeMinor)
        assertEquals(150_000L, result.totalExpenseMinor)
        assertEquals(-50_000L, result.balanceMinor)
    }

    @Test
    fun incomeOnlyTransactionsProduceZeroExpenses() {
        val transactions = listOf(
            transaction(amountMinor = 250_000, type = TransactionType.INCOME)
        )

        val result = calculateFinancialSummary(transactions)

        assertEquals(250_000L, result.totalIncomeMinor)
        assertEquals(0L, result.totalExpenseMinor)
        assertEquals(250_000L, result.balanceMinor)
    }

    private fun transaction(
        amountMinor: Long,
        type: TransactionType
    ) = Transaction(
        amountMinor = amountMinor,
        type = type,
        categoryId = 1,
        note = "",
        transactionDateEpochMillis = 0,
        createdAtEpochMillis = 0
    )
}
