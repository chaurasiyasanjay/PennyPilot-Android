package com.pennypilot.app.di

import com.pennypilot.app.data.repository.ExchangeRateRepositoryImpl
import com.pennypilot.app.domain.repository.ExchangeRateRepository
import com.pennypilot.app.data.repository.CategoryRepositoryImpl
import com.pennypilot.app.data.repository.TransactionRepositoryImpl
import com.pennypilot.app.domain.repository.CategoryRepository
import com.pennypilot.app.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExchangeRateRepository(
        implementation: ExchangeRateRepositoryImpl
    ): ExchangeRateRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        implementation: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        implementation: CategoryRepositoryImpl
    ): CategoryRepository
}
