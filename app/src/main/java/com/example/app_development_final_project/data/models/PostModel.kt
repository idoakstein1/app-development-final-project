package com.example.app_development_final_project.data.models

import com.example.app_development_final_project.base.ListCallback
import com.example.app_development_final_project.data.FirebaseModel
import com.example.app_development_final_project.data.entities.Movie
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.data.entities.User
import kotlin.random.Random

class PostModel private constructor() {
    private val firebase = FirebaseModel()

    companion object {
        val shared = PostModel()
    }

    fun getFeed(callback: ListCallback<Post>) = firebase.getFeed(UserModel.shared.connectedUser.id, callback)

    fun getPostsByUser(userId: String, callback: ListCallback<Post>) = firebase.getPostsByUser(userId, callback)
}