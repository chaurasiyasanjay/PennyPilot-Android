package com.pennypilot.app.presentation.addtransaction

import com.pennypilot.app.domain.model.Category
import com.pennypilot.app.domain.model.TransactionType

data class AddTransactionUiState(
    val transactionId: Long? = null,
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val note: String = "",
    val selectedCategoryId: Long? = null,
    val categories: List<Category> = emptyList(),
    val transactionDateEpochMillis: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)
