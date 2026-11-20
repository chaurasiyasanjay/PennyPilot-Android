package com.pennypilot.app.data.repository

import com.pennypilot.app.data.local.dao.CategoryDao
import com.pennypilot.app.data.mapper.toDomain
import com.pennypilot.app.domain.model.Category
import com.pennypilot.app.domain.model.TransactionType
import com.pennypilot.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeCategoriesByType(type: TransactionType): Flow<List<Category>> =
        categoryDao.observeByType(type.name).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getCategory(id: Long): Category? =
        categoryDao.getById(id)?.toDomain()
}
