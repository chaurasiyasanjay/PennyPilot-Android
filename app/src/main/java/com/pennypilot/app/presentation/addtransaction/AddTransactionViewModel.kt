package com.pennypilot.app.presentation.addtransaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pennypilot.app.domain.model.Category
import com.pennypilot.app.domain.model.Transaction
import com.pennypilot.app.domain.model.TransactionType
import com.pennypilot.app.domain.repository.CategoryRepository
import com.pennypilot.app.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val transactionId = savedStateHandle.get<Any>("transactionId")?.toString()?.toLongOrNull()
    private val mutableUiState = MutableStateFlow(
        AddTransactionUiState(
            transactionId = transactionId,
            isLoading = transactionId != null
        )
    )
    val uiState: StateFlow<AddTransactionUiState> = mutableUiState.asStateFlow()
    private var allCategories: List<Category> = emptyList()
    private var existingTransaction: Transaction? = null

    init {
        viewModelScope.launch {
            categoryRepository.observeCategories()
                .catch { exception ->
                    LOGGER.severe("Unable to observe categories" + ": " + exception.message)
                    mutableUiState.update {
                        it.copy(errorMessage = "Unable to load categories. Please try again.")
                    }
                }
                .collect { categories ->
                    allCategories = categories
                    updateVisibleCategories()
                }
        }

        if (transactionId != null) {
            viewModelScope.launch {
                try {
                    val transaction = transactionRepository.getTransaction(transactionId)
                    if (transaction == null) {
                        mutableUiState.update {
                            it.copy(isLoading = false, errorMessage = "Transaction not found.")
                        }
                    } else {
                        existingTransaction = transaction
                        mutableUiState.update {
                            it.copy(
                                isLoading = false,
                                type = transaction.type,
                                amount = BigDecimal.valueOf(transaction.amountMinor, 2).toPlainString(),
                                note = transaction.note,
                                selectedCategoryId = transaction.categoryId,
                                transactionDateEpochMillis = transaction.transactionDateEpochMillis
                            )
                        }
                        updateVisibleCategories()
                    }
                } catch (exception: Exception) {
                    LOGGER.severe("Unable to load transaction" + ": " + exception.message)
                    mutableUiState.update {
                        it.copy(isLoading = false, errorMessage = "Unable to load transaction.")
                    }
                }
            }
        }
    }

    fun onTypeChanged(type: TransactionType) {
        mutableUiState.update {
            it.copy(type = type, selectedCategoryId = null, errorMessage = null)
        }
        updateVisibleCategories()
    }

    fun onAmountChanged(amount: String) {
        mutableUiState.update { it.copy(amount = amount, errorMessage = null) }
    }

    fun onNoteChanged(note: String) {
        mutableUiState.update { it.copy(note = note, errorMessage = null) }
    }

    fun onCategorySelected(categoryId: Long) {
        mutableUiState.update { it.copy(selectedCategoryId = categoryId, errorMessage = null) }
    }

    fun onDateChanged(dateEpochMillis: Long) {
        mutableUiState.update { it.copy(transactionDateEpochMillis = dateEpochMillis) }
    }

    fun saveTransaction() {
        val state = mutableUiState.value
        val amountMinor = parseAmountToMinorUnits(state.amount)

        if (amountMinor == null) {
            mutableUiState.update {
                it.copy(errorMessage = "Enter a valid amount greater than zero, with up to 2 decimal places.")
            }
            return
        }

        val categoryId = state.selectedCategoryId
        if (categoryId == null || state.categories.none { it.id == categoryId }) {
            mutableUiState.update { it.copy(errorMessage = "Please select a valid category.") }
            return
        }

        viewModelScope.launch {
            mutableUiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val original = existingTransaction
                val transaction = Transaction(
                    id = original?.id ?: 0L,
                    amountMinor = amountMinor,
                    type = state.type,
                    categoryId = categoryId,
                    note = state.note.trim(),
                    transactionDateEpochMillis = state.transactionDateEpochMillis,
                    createdAtEpochMillis = original?.createdAtEpochMillis ?: System.currentTimeMillis()
                )
                if (original == null) {
                    transactionRepository.addTransaction(transaction)
                } else {
                    transactionRepository.updateTransaction(transaction)
                }
                mutableUiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (exception: Exception) {
                LOGGER.severe("Unable to save transaction" + ": " + exception.message)
                mutableUiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Unable to save transaction. Please try again."
                    )
                }
            }
        }
    }

    private fun updateVisibleCategories() {
        mutableUiState.update { state ->
            val visible = allCategories.filter { it.type == state.type }
            state.copy(
                categories = visible,
                selectedCategoryId = state.selectedCategoryId
                    ?.takeIf { selectedId -> visible.any { it.id == selectedId } }
                    ?: visible.firstOrNull()?.id
            )
        }
    }

    private companion object {
        val LOGGER: java.util.logging.Logger = java.util.logging.Logger.getLogger("PennyPilotTransaction")
    }

    private fun parseAmountToMinorUnits(input: String): Long? {
        val amount = input.trim().toBigDecimalOrNull() ?: return null
        if (amount <= BigDecimal.ZERO || amount.scale().coerceAtLeast(0) > 2) return null
        return try {
            amount.movePointRight(2).longValueExact()
        } catch (_: ArithmeticException) {
            null
        }
    }
}
