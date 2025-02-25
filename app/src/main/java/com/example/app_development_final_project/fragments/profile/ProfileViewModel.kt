package com.example.app_development_final_project.fragments.profile

import androidx.lifecycle.ViewModel
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.data.models.UserModel

class ProfileViewModel : ViewModel() {
    var userPosts: List<Post> = listOf()
    var user = UserModel.shared.connectedUser
}
