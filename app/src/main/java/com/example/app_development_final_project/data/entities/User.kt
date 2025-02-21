package com.example.app_development_final_project.data.entities

data class User(
    val username: String,
    val email: String,
    val password: String,
    val profilePicture: String? = ""
)