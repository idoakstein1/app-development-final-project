package com.example.app_development_final_project.fragments.feed

import androidx.lifecycle.ViewModel
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.data.models.PostModel

class FeedViewModel : ViewModel() {
    var posts: List<Post> = listOf()
}