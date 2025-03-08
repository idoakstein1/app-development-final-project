package com.example.app_development_final_project.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app_development_final_project.data.AppLocalDb
import com.example.app_development_final_project.data.FirebaseModel
import com.example.app_development_final_project.data.entities.Post
import java.util.concurrent.Executors

class PostModel private constructor() {
    enum class LoadingState {
        LOADING,
        LOADED
    }

    private val firebase = FirebaseModel()
    private val database = AppLocalDb.database

    private var executor = Executors.newSingleThreadExecutor()

    val feed: LiveData<List<Post>> = database.PostDao().getFeed("ybhrY9WkUZXvLDWzTUUeSPKylA52")
    val userPosts: LiveData<List<Post>> = database.PostDao().getPostsByUser("ybhrY9WkUZXvLDWzTUUeSPKylA52")
    val loadingState: MutableLiveData<LoadingState> = MutableLiveData<LoadingState>()

    companion object {
        val shared = PostModel()
    }

    fun refreshFeed(userId: String) {
        UserModel.shared.refreshUsers {

            loadingState.postValue(LoadingState.LOADING)
            val lastUpdated = Post.lastUpdated

            firebase.getFeed(userId, lastUpdated) { posts ->
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
    }

    fun refreshPostsByUser(userId: String) {
        UserModel.shared.refreshUsers {
        loadingState.postValue(LoadingState.LOADING)
        val lastUpdated = Post.lastUpdated

        firebase.getPostsByUser(userId, lastUpdated) { posts ->
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
    }
}