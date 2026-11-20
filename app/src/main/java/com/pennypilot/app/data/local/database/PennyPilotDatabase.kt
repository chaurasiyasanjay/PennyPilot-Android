package com.pennypilot.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pennypilot.app.data.local.dao.CategoryDao
import com.pennypilot.app.data.local.dao.TransactionDao
import com.pennypilot.app.data.local.entity.CategoryEntity
import com.pennypilot.app.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class PennyPilotDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    abstract fun categoryDao(): CategoryDao
}
