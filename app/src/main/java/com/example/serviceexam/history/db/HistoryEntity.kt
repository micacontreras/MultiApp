package com.example.serviceexam.history.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey (autoGenerate = true) val id: Int,
    @ColumnInfo(name = "photoUri") val photoUri: String,
    @ColumnInfo(name = "name_user") val userName: String
)
