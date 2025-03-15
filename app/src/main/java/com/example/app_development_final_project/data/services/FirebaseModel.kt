package com.example.app_development_final_project.data.services

import com.example.app_development_final_project.base.Constants
import com.example.app_development_final_project.base.EmptyCallback
import com.example.app_development_final_project.base.ListCallback
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.data.entities.User
import com.example.app_development_final_project.data.models.UserModel
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

    fun getFeed(sinceLastUpdated: Long, callback: ListCallback<Post>) {
        val currentUserId = UserModel.shared.connectedUser?.id

        database.collection(Constants.Collections.POSTS)
            .whereGreaterThanOrEqualTo(Post.FieldKeys.LAST_UPDATE_TIME, sinceLastUpdated.toFirebaseTimestamp)
            .orderBy(Post.FieldKeys.LAST_UPDATE_TIME, Query.Direction.DESCENDING)
            .orderBy(Post.FieldKeys.CREATION_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { postsJson ->
                database.collection(Constants.Collections.USERS)
                    .whereNotEqualTo(User.FieldKeys.ID, currentUserId)
                    .get()
                    .addOnSuccessListener { usersJson ->
                        val users = usersJson.map { json -> User.fromJson(json.data) }

                        callback(
                            postsJson
                                .filter { json -> json.data[Post.FieldKeys.USER_ID] != currentUserId }
                                .map { json ->
                                    val user = users.find { user -> user.id == json.data[Post.FieldKeys.USER_ID] }
                                        ?: throw Exception("User not found")

                                    Post.fromJson(json.data).copy(username = user.username, userProfilePicture = user.profilePicture)
                                }
                                .sortedByDescending { it.creationTime }
                        )
                    }.addOnFailureListener { callback(listOf()) }
            }
            .addOnFailureListener { callback(listOf()) }
    }

    fun getPostsByUser(sinceLastUpdated: Long, callback: ListCallback<Post>) {
        database.collection(Constants.Collections.POSTS)
            .whereGreaterThanOrEqualTo(Post.FieldKeys.LAST_UPDATE_TIME, sinceLastUpdated.toFirebaseTimestamp)
            .orderBy(Post.FieldKeys.LAST_UPDATE_TIME, Query.Direction.DESCENDING)
            .orderBy(Post.FieldKeys.CREATION_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { postsJson ->
                callback(
                    postsJson
                        .map { json -> Post.fromJson(json.data) }
                        .filter { post -> post.userId == UserModel.shared.connectedUser?.id }
                        .sortedByDescending { it.creationTime }
                )
            }.addOnFailureListener { callback(listOf()) }
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

    fun updateLastUpdateTimeByUser(userId: String, callback: (Boolean) -> Unit) {
        val currentTime = System.currentTimeMillis().toFirebaseTimestamp

        database.collection(Constants.Collections.POSTS)
            .whereEqualTo(Post.FieldKeys.USER_ID, userId)
            .get()
            .addOnSuccessListener { posts ->
                val batch = database.batch()

                for (post in posts) {
                    batch.update(post.reference, mapOf(Post.FieldKeys.LAST_UPDATE_TIME to currentTime))
                }

                batch.commit()
                    .addOnSuccessListener { callback(true) }
                    .addOnFailureListener { callback(false) }
            }
            .addOnFailureListener { callback(false) }
    }
}