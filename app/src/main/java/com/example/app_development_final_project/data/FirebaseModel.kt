package com.example.app_development_final_project.data

import com.example.app_development_final_project.base.Constants
import com.example.app_development_final_project.base.EmptyCallback
import com.example.app_development_final_project.base.ListCallback
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.data.entities.User
import com.example.app_development_final_project.extensions.toFirebaseTimestamp
import com.google.firebase.firestore.Query
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

    fun getFeed(userId: String, sinceLastUpdated: Long, callback: ListCallback<Post>) {
        database.collection(Constants.Collections.POSTS)
            .whereGreaterThanOrEqualTo(Post.FieldKeys.LAST_UPDATE_TIME, sinceLastUpdated.toFirebaseTimestamp)
            .orderBy(Post.FieldKeys.CREATION_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> callback(
                        it.result
                            .map { json -> Post.fromJson(json.data) }
                            .filter { post -> post.userId != userId }
                    )

                    false -> callback(listOf())
                }
            }
    }

    fun getPostsByUser(userId: String, sinceLastUpdated: Long, callback: ListCallback<Post>) {
        database.collection(Constants.Collections.POSTS)
            .whereGreaterThanOrEqualTo(Post.FieldKeys.LAST_UPDATE_TIME, sinceLastUpdated.toFirebaseTimestamp)
            .orderBy(Post.FieldKeys.CREATION_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> callback(
                        it.result
                            .map { json -> Post.fromJson(json.data) }
                            .filter { post -> post.userId == userId }
                    )

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

    fun getAllUsers(callback: ListCallback<User>) {
        database.collection(Constants.Collections.USERS)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> callback(it.result.map { json -> User.fromJson(json.data) })
                    false -> callback(listOf())
                }
            }
    }
}