package com.example.serviceexam.history.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [History::class], version = 1, exportSchema = false)
abstract class HistoryDataBase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    /*private class HistoryDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var photoDao = database.photoDao()
                }
            }
        }
    }*/

    companion object {
        @Volatile
        private var INSTANCE: HistoryDataBase? = null

        fun getDatabase(
            context: Context
        ): HistoryDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HistoryDataBase::class.java,
                    "HistoryDataBase"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
