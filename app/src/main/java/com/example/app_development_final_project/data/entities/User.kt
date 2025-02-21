package com.example.app_development_final_project.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val username: String,
    val email: String,
    val password: String,
    val profilePicture: String? = ""
) : Parcelable