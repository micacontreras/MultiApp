package com.example.serviceexam.history.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photo(
    @PrimaryKey (autoGenerate = true) val id: Int,
    @ColumnInfo(name = "photo") val photo: String,
    @ColumnInfo(name = "name_user") val userName: String
)
