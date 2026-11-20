package com.pennypilot.app.presentation.addtransaction

import androidx.lifecycle.SavedStateHandle
import com.pennypilot.app.domain.model.Category
import com.pennypilot.app.domain.model.Transaction
import com.pennypilot.app.domain.model.TransactionType
import com.pennypilot.app.domain.repository.CategoryRepository
import com.pennypilot.app.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddTransactionViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var viewModel: AddTransactionViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        transactionRepository = FakeTransactionRepository()
        viewModel = createViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun validExpenseIsSavedInMinorCurrencyUnits() = runTest(dispatcher) {
        advanceUntilIdle()
        viewModel.onAmountChanged("12.50")
        viewModel.onNoteChanged("Lunch")
        viewModel.saveTransaction()
        advanceUntilIdle()

        val saved = transactionRepository.savedTransaction
        assertNotNull(saved)
        assertEquals(1_250L, saved?.amountMinor)
        assertEquals(TransactionType.EXPENSE, saved?.type)
        assertEquals(1L, saved?.categoryId)
        assertEquals("Lunch", saved?.note)
        assertTrue(viewModel.uiState.value.isSaved)
    }

    @Test
    fun invalidAmountShowsValidationErrorAndDoesNotSave() = runTest(dispatcher) {
        advanceUntilIdle()
        viewModel.onAmountChanged("0")
        viewModel.saveTransaction()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isSaved)
        assertEquals(null, transactionRepository.savedTransaction)
    }

    @Test
    fun amountWithMoreThanTwoDecimalPlacesIsRejected() = runTest(dispatcher) {
        advanceUntilIdle()
        viewModel.onAmountChanged("1.999")
        viewModel.saveTransaction()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals(null, transactionRepository.savedTransaction)
    }

    @Test
    fun existingTransactionIsLoadedAndUpdatedWithoutChangingItsIdOrCreationTime() =
        runTest(dispatcher) {
            transactionRepository.savedTransaction = Transaction(
                id = 42L,
                amountMinor = 1_000L,
                type = TransactionType.EXPENSE,
                categoryId = 1L,
                note = "Old note",
                transactionDateEpochMillis = 1_000L,
                createdAtEpochMillis = 500L
            )
            viewModel = createViewModel(SavedStateHandle(mapOf("transactionId" to "42")))

            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertEquals("10.00", viewModel.uiState.value.amount)
            assertEquals("Old note", viewModel.uiState.value.note)

            viewModel.onAmountChanged("25.75")
            viewModel.onNoteChanged("Updated note")
            viewModel.saveTransaction()
            advanceUntilIdle()

            val updated = transactionRepository.savedTransaction
            assertNotNull(updated)
            assertEquals(42L, updated?.id)
            assertEquals(2_575L, updated?.amountMinor)
            assertEquals("Updated note", updated?.note)
            assertEquals(500L, updated?.createdAtEpochMillis)
            assertTrue(viewModel.uiState.value.isSaved)
            assertEquals(1, transactionRepository.updateCount)
        }

    private fun createViewModel(
        savedStateHandle: SavedStateHandle = SavedStateHandle()
    ) = AddTransactionViewModel(
        transactionRepository = transactionRepository,
        categoryRepository = FakeCategoryRepository(),
        savedStateHandle = savedStateHandle
    )

    private class FakeCategoryRepository : CategoryRepository {
        private val categories = listOf(
            Category(id = 1, name = "Food", type = TransactionType.EXPENSE),
            Category(id = 2, name = "Salary", type = TransactionType.INCOME)
        )

        override fun observeCategories(): Flow<List<Category>> = flowOf(categories)

        override fun observeCategoriesByType(type: TransactionType): Flow<List<Category>> =
            flowOf(categories.filter { it.type == type })

        override suspend fun getCategory(id: Long): Category? =
            categories.firstOrNull { it.id == id }
    }

    private class FakeTransactionRepository : TransactionRepository {
        var savedTransaction: Transaction? = null
        var updateCount: Int = 0

        override fun observeTransactions(): Flow<List<Transaction>> =
            MutableStateFlow(savedTransaction?.let(::listOf) ?: emptyList())

        override fun observeTransactionsByCategory(categoryId: Long): Flow<List<Transaction>> =
            MutableStateFlow(emptyList())

        override suspend fun getTransaction(id: Long): Transaction? =
            savedTransaction?.takeIf { it.id == id }

        override suspend fun addTransaction(transaction: Transaction): Long {
            savedTransaction = transaction.copy(id = 1L)
            return 1L
        }

        override suspend fun updateTransaction(transaction: Transaction) {
            savedTransaction = transaction
            updateCount++
        }

        override suspend fun deleteTransaction(transaction: Transaction) = Unit
    }
}
