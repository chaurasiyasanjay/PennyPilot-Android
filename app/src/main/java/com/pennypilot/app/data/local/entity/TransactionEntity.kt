package com.pennypilot.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["transactionDateEpochMillis"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    /** Amount in the currency's minor unit (for example, cents). */
    val amountMinor: Long,
    /** INCOME or EXPENSE. Kept as a string to avoid persistence converters. */
    val type: String,
    val categoryId: Long,
    val note: String,
    val transactionDateEpochMillis: Long,
    val createdAtEpochMillis: Long
)
