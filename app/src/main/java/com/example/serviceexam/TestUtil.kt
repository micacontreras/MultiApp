package com.example.serviceexam

import com.example.serviceexam.history.db.History

object TestUtil {

    fun insertPhoto(id: Int, uri: String, name: String) = History(
        id = id,
        photoUri = uri,
        userName = name
    )
}