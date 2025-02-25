package com.example.app_development_final_project.data.models

import com.example.app_development_final_project.base.EmptyCallback
import com.example.app_development_final_project.base.OptionalCallback
import com.example.app_development_final_project.data.FirebaseModel
import com.example.app_development_final_project.data.entities.User

class UserModel private constructor() {
    private val firebase = FirebaseModel()

    var connectedUser = User(id = "1", username = "ido", email = "ido@gmail.com", password = "ido")

    companion object {
        val shared = UserModel()
    }

    fun updateUser(user: User, callback: EmptyCallback) {
        firebase.updateUser(user, callback)
    }
}