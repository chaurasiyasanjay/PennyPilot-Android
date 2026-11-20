package com.pennypilot.app.di

import com.pennypilot.app.data.local.preferences.SettingsDataStore
import com.pennypilot.app.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        implementation: SettingsDataStore
    ): SettingsRepository
}
