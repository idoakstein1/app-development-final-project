package com.example.app_development_final_project.data.models

import com.example.app_development_final_project.base.EmptyCallback
import com.example.app_development_final_project.base.ListCallback
import com.example.app_development_final_project.data.AppLocalDb
import com.example.app_development_final_project.data.FirebaseModel
import com.example.app_development_final_project.data.entities.User
import java.util.concurrent.Executors

class UserModel private constructor() {
    private val firebase = FirebaseModel()
    private val database = AppLocalDb.database

    private var executor = Executors.newSingleThreadExecutor()

    var connectedUser = User(id = "1", username = "ido", email = "ido@gmail.com", password = "ido")

    companion object {
        val shared = UserModel()
    }

    fun refreshUsers(callback: ListCallback<User>) {
        firebase.getAllUsers { users ->
            executor.execute {
                for (user in users) {
                    database.UserDao().createUser(user)
                }
            }
            callback(users)
        }
    }

    fun updateUser(user: User, callback: EmptyCallback) {
        firebase.createUser(user, callback)
    }
}