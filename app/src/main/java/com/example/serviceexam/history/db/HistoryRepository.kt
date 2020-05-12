package com.example.serviceexam.history.db

import androidx.lifecycle.LiveData

class HistoryRepository(private val photoDao: PhotoDao) {

    val allPhotos: LiveData<List<Photo>> = photoDao.getAll()
    //val userName: LiveData<String> = photoDao.getUserName(Photo().id)
    //val datePhoto: LiveData<String> = photoDao.getPhotoDate()

    suspend fun insert(photo: Photo) {
        photoDao.insertPhoto(photo)
    }
    suspend fun delete(photo: Photo){
        photoDao.deletePhoto(photo)
    }
}