package com.pennypilot.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pennypilot.app.data.local.dao.CategoryDao
import com.pennypilot.app.data.local.dao.TransactionDao
import com.pennypilot.app.data.local.database.PennyPilotDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePennyPilotDatabase(
        @ApplicationContext context: Context
    ): PennyPilotDatabase =
        Room.databaseBuilder(
            context,
            PennyPilotDatabase::class.java,
            "penny_pilot_database"
        )
            .addCallback(DefaultCategoryCallback())
            .build()

    @Provides
    fun provideTransactionDao(database: PennyPilotDatabase): TransactionDao =
        database.transactionDao()

    @Provides
    fun provideCategoryDao(database: PennyPilotDatabase): CategoryDao =
        database.categoryDao()
}

/**
 * Inserts the built-in categories once, when Room creates the database for the first time.
 *
 * INSERT OR IGNORE plus the unique (name, type) index makes this safe if the seed
 * operation is ever repeated.
 */
private class DefaultCategoryCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        val defaultCategories = listOf(
            Triple("Food", "EXPENSE", "🍽️"),
            Triple("Transport", "EXPENSE", "🚗"),
            Triple("Shopping", "EXPENSE", "🛍️"),
            Triple("Bills", "EXPENSE", "🧾"),
            Triple("Health", "EXPENSE", "❤️"),
            Triple("Entertainment", "EXPENSE", "🎬"),
            Triple("Other", "EXPENSE", "📦"),
            Triple("Salary", "INCOME", "💼"),
            Triple("Business", "INCOME", "🏪"),
            Triple("Investment", "INCOME", "📈"),
            Triple("Other", "INCOME", "💰")
        )

        defaultCategories.forEach { (name, type, icon) ->
            db.execSQL(
                """
                INSERT OR IGNORE INTO categories (name, type, icon, isDefault)
                VALUES (?, ?, ?, 1)
                """.trimIndent(),
                arrayOf(name, type, icon)
            )
        }
    }
}
