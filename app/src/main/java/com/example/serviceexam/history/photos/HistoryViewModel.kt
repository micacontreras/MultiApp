package com.example.serviceexam.history.photos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.serviceexam.history.db.HistoryDataBase
import com.example.serviceexam.history.db.HistoryRepository
import com.example.serviceexam.history.db.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository

    val allPhoto: LiveData<List<Photo>>

    init {
        val photoDao = HistoryDataBase.getDatabase(application).photoDao()
        repository = HistoryRepository(photoDao)
        allPhoto = repository.allPhotos
    }

    fun insert(photo: Photo) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(photo)
    }
    fun delete(photo: Photo)= viewModelScope.launch(Dispatchers.IO) {
        repository.delete(photo)
    }
}