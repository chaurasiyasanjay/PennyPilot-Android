package com.pennypilot.app.data.mapper

import com.pennypilot.app.data.local.entity.TransactionEntity
import com.pennypilot.app.domain.model.Transaction
import com.pennypilot.app.domain.model.TransactionType

/**
 * Converts between Room's transaction representation and the domain model.
 */
fun TransactionEntity.toDomain(): Transaction =
    Transaction(
        id = id,
        amountMinor = amountMinor,
        type = TransactionType.valueOf(type),
        categoryId = categoryId,
        note = note,
        transactionDateEpochMillis = transactionDateEpochMillis,
        createdAtEpochMillis = createdAtEpochMillis
    )

fun Transaction.toEntity(): TransactionEntity =
    TransactionEntity(
        id = id,
        amountMinor = amountMinor,
        type = type.name,
        categoryId = categoryId,
        note = note,
        transactionDateEpochMillis = transactionDateEpochMillis,
        createdAtEpochMillis = createdAtEpochMillis
    )
