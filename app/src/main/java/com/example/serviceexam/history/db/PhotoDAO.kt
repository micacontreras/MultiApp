package com.example.serviceexam.history.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photo")
    fun getAll(): LiveData<List<Photo>>

    @Insert
    suspend fun insertPhoto(vararg photo: Photo)

    @Delete
    suspend fun deletePhoto(photo: Photo)

}
