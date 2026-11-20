package com.pennypilot.app.domain.repository

import com.pennypilot.app.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Defines the transaction operations required by the application.
 *
 * Implementations belong to the data layer; callers do not need to know
 * whether transactions are stored in Room or another data source.
 */
interface TransactionRepository {

    fun observeTransactions(): Flow<List<Transaction>>

    fun observeTransactionsByCategory(categoryId: Long): Flow<List<Transaction>>

    suspend fun getTransaction(id: Long): Transaction?

    suspend fun addTransaction(transaction: Transaction): Long

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)
}
