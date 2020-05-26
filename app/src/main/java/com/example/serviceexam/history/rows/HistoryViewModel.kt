package com.example.serviceexam.history.rows

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.serviceexam.history.db.History
import com.example.serviceexam.history.db.HistoryDataBase
import com.example.serviceexam.history.db.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository

    val allItems: LiveData<List<History>>

    init {
        val historyDao = HistoryDataBase.getDatabase(application).historyDao()
        repository = HistoryRepository(historyDao)
        allItems = repository.allPhotos
    }

    fun insert(history: History) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(history)
    }
    fun delete(photoUri: String)= viewModelScope.launch(Dispatchers.IO) {
        repository.delete(photoUri)
    }
}