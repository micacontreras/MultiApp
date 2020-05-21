package com.example.serviceexam.history.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history")
    fun getAll(): LiveData<List<History>>

    @Insert
    suspend fun insert(vararg history: History)

    @Query("DELETE FROM history WHERE photoUri = :photoUri")
    suspend fun delete(photoUri: String)

}
