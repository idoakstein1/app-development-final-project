package com.example.app_development_final_project.data

import com.example.app_development_final_project.base.Constants
import com.example.app_development_final_project.base.EmptyCallback
import com.example.app_development_final_project.base.ListCallback
import com.example.app_development_final_project.base.OptionalCallback
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.data.entities.User
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase

class FirebaseModel {
    private var database = Firebase.firestore

    init {
        database.firestoreSettings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings { })
        }
    }

    fun getFeed(userId: String, callback: ListCallback<Post>) {
        database.collection(Constants.Collections.POSTS)
            .whereNotEqualTo("user.id", userId)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val posts = it.result.map { json -> Post.fromJson(json.data) }
                        callback(posts)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun getPostsByUser(userId: String, callback: ListCallback<Post>) {
        database.collection(Constants.Collections.POSTS)
            .whereEqualTo("user.id", userId)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val posts = it.result.map { json -> Post.fromJson(json.data) }
                        callback(posts)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun createPost(post: Post, callback: EmptyCallback) {
        database.collection(Constants.Collections.POSTS)
            .document(post.id)
            .set(post.json)
            .addOnCompleteListener { callback() }
    }

    fun deletePost(postId: String, callback: EmptyCallback) {
        database.collection(Constants.Collections.POSTS)
            .document(postId)
            .delete()
            .addOnCompleteListener { callback() }
    }

    fun createUser(user: User, callback: EmptyCallback) {
        database.collection(Constants.Collections.USERS)
            .document(user.id)
            .set(user.json)
            .addOnCompleteListener { callback() }
    }

    fun updateUser(newUser: User, callback: EmptyCallback) {
        database.collection(Constants.Collections.USERS)
            .document(newUser.id)
            .set(newUser.json)
            .addOnSuccessListener {
                updateUserInPosts(newUser, callback)
            }
            .addOnFailureListener { callback() }
    }

    private fun updateUserInPosts(newUser: User, callback: EmptyCallback) {
        database.collection(Constants.Collections.POSTS)
            .whereEqualTo("user.id", newUser.id)
            .get()
            .addOnSuccessListener {
                val batch = database.batch()

                for (post in it.documents) {
                    batch.update(post.reference, "user", newUser.json)
                }

                batch.commit().addOnCompleteListener { callback() }
            }
            .addOnFailureListener { callback() }
    }
}