package com.pennypilot.app.domain.model

/**
 * A transaction represented in the application's business layer.
 *
 * [amountMinor] is stored in the base currency's minor unit (MYR sen).
 * The selected display currency is a presentation preference and does not
 * change the amount persisted for the transaction.
 */
data class Transaction(
    val id: Long = 0,
    val amountMinor: Long,
    val type: TransactionType,
    val categoryId: Long,
    val note: String,
    val transactionDateEpochMillis: Long,
    val createdAtEpochMillis: Long
)
