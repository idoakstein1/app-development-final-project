package com.example.app_development_final_project.fragments.profile

import androidx.lifecycle.ViewModel
import com.example.app_development_final_project.data.models.PostModel
import com.example.app_development_final_project.data.models.UserModel

class ProfileViewModel : ViewModel() {
    var userPosts = PostModel.shared.userPosts
    var user = UserModel.shared.connectedUserLive

    fun refreshUserPosts() {
        PostModel.shared.refreshPostsByUser()
    }
}
