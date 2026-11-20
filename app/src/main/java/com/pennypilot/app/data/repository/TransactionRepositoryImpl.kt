package com.pennypilot.app.data.repository

import com.pennypilot.app.data.local.dao.TransactionDao
import com.pennypilot.app.data.mapper.toDomain
import com.pennypilot.app.data.mapper.toEntity
import com.pennypilot.app.domain.model.Transaction
import com.pennypilot.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun observeTransactions(): Flow<List<Transaction>> =
        transactionDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeTransactionsByCategory(categoryId: Long): Flow<List<Transaction>> =
        transactionDao.observeByCategory(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getTransaction(id: Long): Transaction? =
        transactionDao.getById(id)?.toDomain()

    override suspend fun addTransaction(transaction: Transaction): Long =
        transactionDao.insert(transaction.toEntity())

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction.toEntity())
    }
}
