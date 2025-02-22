package com.example.app_development_final_project.fragments.profile

import androidx.lifecycle.ViewModel
import com.example.app_development_final_project.data.entities.User
import com.example.app_development_final_project.data.models.PostModel

class ProfileViewModel : ViewModel() {
    var userPosts = PostModel.shared.getPostsByUser("ido")
    var user = User(username = "ido", email = "ido@gmail.com", password = "ido")
}