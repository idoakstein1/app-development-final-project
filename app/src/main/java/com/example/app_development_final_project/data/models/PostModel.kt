package com.example.app_development_final_project.data.models

import com.example.app_development_final_project.data.entities.Movie
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.data.entities.User
import kotlin.random.Random

class PostModel private constructor() {
    private val posts = mutableListOf<Post>()

    companion object {
        val shared = PostModel()
    }

    fun getAllPosts() = posts

    fun getPostsByUser(username: String) = posts.filter { it.user.username == username }

    init {
        (1..10).forEach { index ->
            posts.add(
                Post(
                    id = index.toString(),
                    user = User(
                        username = if (index % 3 != 0) "ido" else "ido2",
                        email = "ido",
                        password = "ido",
                    ),
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