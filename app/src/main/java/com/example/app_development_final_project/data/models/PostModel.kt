package com.example.app_development_final_project.data.models

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.example.app_development_final_project.base.Callback
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.data.services.AppLocalDb
import com.example.app_development_final_project.data.services.CloudinaryModel
import com.example.app_development_final_project.data.services.FirebaseModel
import java.util.concurrent.Executors

class PostModel private constructor() {
    enum class LoadingState {
        LOADING,
        LOADED
    }

    private val firebase = FirebaseModel()
    private val database = AppLocalDb.database
    private val cloudinaryModel = CloudinaryModel()

    private var executor = Executors.newSingleThreadExecutor()

    val loadingState: MutableLiveData<LoadingState> = MutableLiveData<LoadingState>()
    val feed = MutableLiveData<List<Post>>()
    val userPosts = MutableLiveData<List<Post>>()

    init {
        UserModel.shared.connectedUserLive.observeForever { user ->
            if (user == null) {
                feed.postValue(emptyList())
                userPosts.postValue(emptyList())
            } else {
                database.PostDao().getFeed(user.id).observeForever { feed.postValue(it) }
                database.PostDao().getPostsByUser(user.id).observeForever { userPosts.postValue(it) }
            }
        }
    }

    companion object {
        val shared = PostModel()
    }

    fun refreshFeed() {
        loadingState.postValue(LoadingState.LOADING)
        val lastUpdated = Post.lastUpdated

        firebase.getFeed(lastUpdated) { posts ->
            executor.execute {
                var currentTime = lastUpdated

                for (post in posts) {
                    database.PostDao().createPost(post)
                    post.lastUpdateTime?.let {
                        if (currentTime < it) {
                            currentTime = it
                        }
                    }
                }

                Post.lastUpdated = currentTime
                loadingState.postValue(LoadingState.LOADED)
            }
        }
    }

    fun refreshPostsByUser() {
        loadingState.postValue(LoadingState.LOADING)
        val lastUpdated = Post.lastUpdated

        firebase.getPostsByUser(lastUpdated) { posts ->
            executor.execute {
                var currentTime = lastUpdated

                for (post in posts) {
                    database.PostDao().createPost(post)
                    post.lastUpdateTime?.let {
                        if (currentTime < it) {
                            currentTime = it
                        }
                    }
                }

                Post.lastUpdated = currentTime
                loadingState.postValue(LoadingState.LOADED)
            }
        }
    }

    fun createPost(post: Post, image: Bitmap?, callback: Callback<Pair<Boolean, String?>>) {
        firebase.createPost(post) { isSuccessful ->
            if (isSuccessful) {
                image?.let {
                    cloudinaryModel.uploadImage(it, post.id) { uri ->
                        if (!uri.isNullOrBlank()) {
                            val newPost = post.copy(photoUrl = uri)
                            firebase.createPost(newPost) { isSuccessful ->
                                callback(Pair(isSuccessful, if (isSuccessful) null else "Can't create post, please try again"))
                            }
                        } else {
                            callback(Pair(true, null))
                        }
                    }
                } ?: callback(Pair(true, null))
            } else {
                callback(Pair(false, "Can't create post, please try again"))
            }
        }
    }

    fun updatePost(post: Post, image: Bitmap?, callback: Callback<Pair<Boolean, String?>>) {
        createPost(post, image) { (isSuccessful, errorMessage) ->
            callback(Pair(isSuccessful, if (errorMessage == null) null else "Can't update post, please try again"))
        }
    }

    fun deletePost(postId: String, callback: Callback<Pair<Boolean, String?>>) {
        firebase.deletePost(postId) { isSuccessful ->
            if (isSuccessful) {
                executor.execute {
                    database.PostDao().deletePost(postId)
                }
            }
            callback(Pair(isSuccessful, if (isSuccessful) null else "Can't delete post, please try again"))
        }
    }
}