package com.pennypilot.app.domain.model

/**
 * Identifies whether a transaction records money coming in or going out.
 *
 * The same type is used by transactions and their categories.
 */
enum class TransactionType {
    INCOME,
    EXPENSE
}
