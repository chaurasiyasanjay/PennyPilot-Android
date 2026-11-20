package com.pennypilot.app.domain.model

/**
 * A transaction category used to organize income and expenses.
 */
data class Category(
    val id: Long = 0,
    val name: String,
    val type: TransactionType,
    val icon: String? = null,
    val isDefault: Boolean = true
)
