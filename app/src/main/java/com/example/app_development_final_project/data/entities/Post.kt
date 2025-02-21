package com.example.app_development_final_project.data.entities

data class Post(
    val id: String,
    val user: User,
    val content: String,
    val movie: Movie,
    val rating: Float,
    val photoUrl: String? = "",
)