package com.example.app_development_final_project.data.models

import com.example.app_development_final_project.data.entities.Movie
import com.example.app_development_final_project.data.entities.Post
import kotlin.random.Random

class PostModel private constructor() {
    private val posts = mutableListOf<Post>()

    companion object {
        val shared = PostModel()
    }

    fun getAllPosts() = posts

    init {
        (1..10).forEach { index ->
            posts.add(
                Post(
                    id = index.toString(),
                    user = "ido",
                    content = "this is a content",
                    rating = Random.nextInt(1, 6).toFloat(),
                    movie = Movie(
                        id = index.toString(),
                        name = "movie title",
                        rating = Random.nextInt(1, 6).toFloat()
                    )
                )
            )
        }
    }
}