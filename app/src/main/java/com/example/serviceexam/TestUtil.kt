package com.example.serviceexam

import com.example.serviceexam.history.db.History
import com.example.serviceexam.repositories.network.Properties
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object TestUtil {

    fun insertPhoto(id: Int, uri: String, name: String) = History(
        id = id,
        photoUri = uri,
        userName = name
    )
}