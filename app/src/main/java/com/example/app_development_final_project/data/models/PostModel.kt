package com.example.app_development_final_project.data.models

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.example.app_development_final_project.base.EmptyCallback
import com.example.app_development_final_project.data.services.AppLocalDb
import com.example.app_development_final_project.data.services.FirebaseModel
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.data.services.CloudinaryModel
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

    fun createPost(post: Post, image: Bitmap?, callback: EmptyCallback) {
        firebase.createPost(post) {
            image?.let {
                cloudinaryModel.uploadImage(it, post.id) { uri ->
                    if (!uri.isNullOrBlank()) {
                        val newPost = post.copy(photoUrl = uri)
                        firebase.createPost(newPost, callback)
                    } else {
                        callback()
                    }
                }
            } ?: callback()
        }
    }
}