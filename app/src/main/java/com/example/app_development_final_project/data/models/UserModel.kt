package com.example.app_development_final_project.data.models

import com.example.app_development_final_project.auth.AuthManager
import com.example.app_development_final_project.base.EmptyCallback
import com.example.app_development_final_project.base.OptionalCallback
import com.example.app_development_final_project.data.AppLocalDb
import com.example.app_development_final_project.data.FirebaseModel
import com.example.app_development_final_project.data.entities.User
import java.util.concurrent.Executors

class UserModel private constructor() {
    private val firebase = FirebaseModel()
    private val database = AppLocalDb.database

    private var executor = Executors.newSingleThreadExecutor()

    var connectedUserId = AuthManager.shared.getCurrentUser()

    companion object {
        val shared = UserModel()
    }

    fun createUser(user: User, callback: EmptyCallback) {
        firebase.createUser(user, callback)
    }

    fun getUser(userId: String, callback: OptionalCallback<User>) {
        firebase.getUser(userId, callback)
    }

    fun refreshUsers() {
        firebase.getAllUsers { users ->
            executor.execute {
                database.UserDao().createUsers(users)
            }
        }
    }
}