package com.example.serviceexam.history.photos

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

    val allPhoto: LiveData<List<History>>

    init {
        val photoDao = HistoryDataBase.getDatabase(application).historyDao()
        repository = HistoryRepository(photoDao)
        allPhoto = repository.allPhotos
    }

    fun insert(history: History) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(history)
    }
    fun delete(history: History)= viewModelScope.launch(Dispatchers.IO) {
        repository.delete(history)
    }
}