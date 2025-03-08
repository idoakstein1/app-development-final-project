package com.example.app_development_final_project.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _connectedUser = MutableLiveData<User?>()
    val connectedUserLive: LiveData<User?> get() = _connectedUser

    var connectedUser: User?
        get() = _connectedUser.value
        set(value) {
            _connectedUser.postValue(value)
        }

    companion object {
        val shared = UserModel()
    }

    fun updateUser(user: User, callback: EmptyCallback) {
        connectedUser = user
        createUser(user) {
            firebase.updateLastUpdateTimeByUser(user.id) { isSuccessful ->
                if (isSuccessful) {
                    callback()
                }
            }
        }
    }

    fun createUser(user: User, callback: EmptyCallback) {
        firebase.createUser(user) {
            executor.execute {
                database.UserDao().createUser(user)
            }
            callback()
        }
    }

    fun refreshUsers() {
        firebase.getAllUsers { users ->
            executor.execute {
                database.UserDao().createUsers(users)
            }
        }
    }

    fun getUserById(userId: String, callback: OptionalCallback<User>) {
        executor.execute {
            callback(database.UserDao().getUserById(userId))
        }
    }
}