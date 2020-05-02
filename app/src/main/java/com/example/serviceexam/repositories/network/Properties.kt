package com.example.serviceexam.repositories.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Properties(
    val id: Int,
    val full_name: String,
    val description: String?,
    val owner: Owner
    ) : Parcelable

@Parcelize
data class Owner(val login: String, @Json(name = "avatar_url") val imgSrcUrl: String) : Parcelable
