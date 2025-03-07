package com.example.app_development_final_project.fragments.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.app_development_final_project.auth.AuthManager
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.data.models.PostModel
import com.example.app_development_final_project.data.models.UserModel

class FeedViewModel : ViewModel() {
    var posts: LiveData<List<Post>> = PostModel.shared.feed

    fun refreshFeed() {
        PostModel.shared.refreshFeed()
    }
}