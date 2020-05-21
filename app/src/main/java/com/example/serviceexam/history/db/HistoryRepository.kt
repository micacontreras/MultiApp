package com.example.serviceexam.history.db

import androidx.lifecycle.LiveData

class HistoryRepository(private val historyDao: HistoryDao) {

    val allPhotos: LiveData<List<History>> = historyDao.getAll()

    suspend fun insert(history: History) {
        historyDao.insert(history)
    }
    suspend fun delete(photoUri: String){
        historyDao.delete(photoUri)
    }
}