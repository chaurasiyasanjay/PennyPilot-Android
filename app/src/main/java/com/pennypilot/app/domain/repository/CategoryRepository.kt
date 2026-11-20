package com.pennypilot.app.domain.repository

import com.pennypilot.app.domain.model.Category
import com.pennypilot.app.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

/**
 * Defines the category queries required by the application.
 */
interface CategoryRepository {

    fun observeCategories(): Flow<List<Category>>

    fun observeCategoriesByType(type: TransactionType): Flow<List<Category>>

    suspend fun getCategory(id: Long): Category?
}
