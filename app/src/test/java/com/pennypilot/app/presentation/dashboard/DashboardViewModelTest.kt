package com.pennypilot.app.presentation.dashboard

import com.pennypilot.app.domain.model.AppSettings
import com.pennypilot.app.domain.model.Category
import com.pennypilot.app.domain.model.ExchangeRate
import com.pennypilot.app.domain.model.ThemePreference
import com.pennypilot.app.domain.model.Transaction
import com.pennypilot.app.domain.model.TransactionType
import com.pennypilot.app.domain.repository.CategoryRepository
import com.pennypilot.app.domain.repository.SettingsRepository
import com.pennypilot.app.domain.repository.TransactionRepository
import com.pennypilot.app.domain.usecase.CalculateFinancialSummaryUseCase
import com.pennypilot.app.domain.usecase.GetExchangeRateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateIsLoading() = runTest(testDispatcher) {
        val repository = FakeTransactionRepository(MutableStateFlow(emptyList()))
        val viewModel = createViewModel(repository)

        assertTrue(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.transactions.isEmpty())
    }

    @Test
    fun transactionsUpdateDashboardAndFinancialSummary() = runTest(testDispatcher) {
        val transactions = MutableStateFlow(
            listOf(
                transaction(500_000, TransactionType.INCOME, categoryId = 10),
                transaction(125_000, TransactionType.EXPENSE, categoryId = 1)
            )
        )
        val viewModel = createViewModel(FakeTransactionRepository(transactions))
        val collection = backgroundScope.launch { viewModel.uiState.collect() }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.transactions.size)
        assertEquals(500_000L, state.financialSummary.totalIncomeMinor)
        assertEquals(125_000L, state.financialSummary.totalExpenseMinor)
        assertEquals(375_000L, state.financialSummary.balanceMinor)
        assertEquals("Food", state.categoryNames[1L])

        collection.cancel()
    }

    @Test
    fun selectingCategoryFiltersTransactionsButKeepsOverallFinancialSummary() = runTest(testDispatcher) {
        val transactions = MutableStateFlow(
            listOf(
                transaction(500_000, TransactionType.INCOME, categoryId = 10),
                transaction(125_000, TransactionType.EXPENSE, categoryId = 1)
            )
        )
        val viewModel = createViewModel(FakeTransactionRepository(transactions))
        val collection = backgroundScope.launch { viewModel.uiState.collect() }

        advanceUntilIdle()
        viewModel.onCategorySelected(1L)
        advanceUntilIdle()

        val filteredState = viewModel.uiState.value
        assertEquals(2, filteredState.transactions.size)
        assertEquals(1, filteredState.visibleTransactions.size)
        assertEquals(1L, filteredState.visibleTransactions.single().categoryId)
        assertEquals(500_000L, filteredState.financialSummary.totalIncomeMinor)
        assertEquals(125_000L, filteredState.financialSummary.totalExpenseMinor)
        assertEquals(375_000L, filteredState.financialSummary.balanceMinor)

        viewModel.onCategorySelected(null)
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.visibleTransactions.size)

        collection.cancel()
    }

    @Test
    fun repositoryFailureProducesErrorState() = runTest(testDispatcher) {
        val repository = FakeTransactionRepository(
            transactions = flow { throw IllegalStateException("Database unavailable") }
        )
        val viewModel = createViewModel(repository)
        val collection = backgroundScope.launch { viewModel.uiState.collect() }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorMessage)
        assertEquals(
            "Unable to load dashboard data. Please try again.",
            state.errorMessage
        )
        assertTrue(state.transactions.isEmpty())
        assertNull(state.errorMessage?.takeIf { it.contains("Database unavailable") })

        collection.cancel()
    }

    private fun createViewModel(
        repository: TransactionRepository,
        currency: String = "MYR",
        exchangeRate: Double = 1.0,
        shouldFailRate: Boolean = false
    ) = DashboardViewModel(
        repository,
        FakeCategoryRepository(),
        FakeSettingsRepository(currency),
        GetExchangeRateUseCase(FakeExchangeRateRepository(exchangeRate, shouldFailRate)),
        CalculateFinancialSummaryUseCase()
    )

    @Test
    fun selectedCurrencyLoadsRateAndExposesItForDisplay() = runTest(testDispatcher) {
        val viewModel = createViewModel(
            FakeTransactionRepository(flowOf(emptyList())),
            currency = "SGD",
            exchangeRate = 3.1
        )
        val collection = backgroundScope.launch { viewModel.uiState.collect() }

        advanceUntilIdle()

        assertEquals("SGD", viewModel.uiState.value.displayCurrency)
        assertEquals(3.1, viewModel.uiState.value.exchangeRate!!, 0.0001)
        assertNull(viewModel.uiState.value.exchangeRateErrorMessage)
        collection.cancel()
    }

    @Test
    fun failedRateRequestFallsBackToMyrAndShowsWarning() = runTest(testDispatcher) {
        val viewModel = createViewModel(
            FakeTransactionRepository(flowOf(emptyList())),
            currency = "SGD",
            shouldFailRate = true
        )
        val collection = backgroundScope.launch { viewModel.uiState.collect() }

        advanceUntilIdle()

        assertEquals("MYR", viewModel.uiState.value.displayCurrency)
        assertNull(viewModel.uiState.value.exchangeRate)
        assertNotNull(viewModel.uiState.value.exchangeRateErrorMessage)
        collection.cancel()
    }

    private fun transaction(
        amountMinor: Long,
        type: TransactionType,
        categoryId: Long = 1L
    ) =
        Transaction(
            amountMinor = amountMinor,
            type = type,
            categoryId = categoryId,
            note = "",
            transactionDateEpochMillis = 0,
            createdAtEpochMillis = 0
        )


    private class FakeCategoryRepository : CategoryRepository {
        override fun observeCategories(): Flow<List<Category>> = flowOf(
            listOf(
                Category(id = 1, name = "Food", type = TransactionType.EXPENSE),
                Category(id = 10, name = "Salary", type = TransactionType.INCOME)
            )
        )

        override fun observeCategoriesByType(type: TransactionType): Flow<List<Category>> =
            observeCategories().map { categories -> categories.filter { it.type == type } }

        override suspend fun getCategory(id: Long): Category? =
            observeCategories().first().firstOrNull { it.id == id }
    }

    private class FakeSettingsRepository(currency: String) : SettingsRepository {
        private val settings = MutableStateFlow(AppSettings(displayCurrency = currency))
        override fun observeSettings(): Flow<AppSettings> = settings
        override suspend fun setThemePreference(themePreference: ThemePreference) {
            settings.value = settings.value.copy(themePreference = themePreference)
        }
        override suspend fun setDisplayCurrency(currencyCode: String) {
            settings.value = settings.value.copy(displayCurrency = currencyCode)
        }
    }

    private class FakeExchangeRateRepository(
        private val rate: Double,
        private val shouldFail: Boolean
    ) : com.pennypilot.app.domain.repository.ExchangeRateRepository {
        override suspend fun getLatestRate(baseCurrency: String, quoteCurrency: String): ExchangeRate {
            if (shouldFail) error("Network unavailable")
            return ExchangeRate(baseCurrency, quoteCurrency, rate, "2026-11-18")
        }
    }

    private class FakeTransactionRepository(
        private val transactions: Flow<List<Transaction>>
    ) : TransactionRepository {

        override fun observeTransactions(): Flow<List<Transaction>> = transactions

        override fun observeTransactionsByCategory(categoryId: Long): Flow<List<Transaction>> =
            transactions

        override suspend fun getTransaction(id: Long): Transaction? = null

        override suspend fun addTransaction(transaction: Transaction): Long = 1L

        override suspend fun updateTransaction(transaction: Transaction) = Unit

        override suspend fun deleteTransaction(transaction: Transaction) = Unit
    }
}
