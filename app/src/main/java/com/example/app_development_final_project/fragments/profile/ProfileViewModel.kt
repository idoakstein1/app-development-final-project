package com.example.app_development_final_project.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.data.models.PostModel
import com.example.app_development_final_project.data.models.UserModel

class ProfileViewModel : ViewModel() {
    var userPosts: LiveData<List<Post>> = PostModel.shared.userPosts
    var user = UserModel.shared.connectedUser

    fun refreshUserPosts() {
        PostModel.shared.refreshPostsByUser(user.id)
    }
}
