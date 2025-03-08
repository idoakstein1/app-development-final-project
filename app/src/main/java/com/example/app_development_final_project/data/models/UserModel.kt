package com.example.app_development_final_project.data.models

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app_development_final_project.base.EmptyCallback
import com.example.app_development_final_project.base.OptionalCallback
import com.example.app_development_final_project.services.AppLocalDb
import com.example.app_development_final_project.services.FirebaseModel
import com.example.app_development_final_project.data.entities.User
import com.example.app_development_final_project.services.CloudinaryModel
import java.util.concurrent.Executors

class UserModel private constructor() {
    private val firebase = FirebaseModel()
    private val database = AppLocalDb.database
    private val cloudinaryModel = CloudinaryModel()

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
        createUser(user, null) {
            firebase.updateLastUpdateTimeByUser(user.id) { isSuccessful ->
                if (isSuccessful) {
                    callback()
                }
            }
        }
    }

    fun createUser(user: User, profilePicture: Bitmap?, callback: EmptyCallback) {
        val createUserInRoom = {
            executor.execute {
                database.UserDao().createUser(user)
            }
            callback()
        }

        firebase.createUser(user) {
            profilePicture?.let {
                uploadImage(it, user.id) { uri ->
                    if (!uri.isNullOrBlank()) {
                        val newUser = user.copy(profilePicture = uri)
                        firebase.createUser(newUser, createUserInRoom)
                    } else {
                        createUserInRoom()
                    }
                }
            } ?: createUserInRoom()
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

    private fun uploadImage(image: Bitmap, name: String, callback: OptionalCallback<String>) {
        cloudinaryModel.uploadImage(
            bitmap = image,
            name = name,
            onSuccess = callback,
            onError = { callback(null) }
        )
    }
}