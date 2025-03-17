package com.example.app_development_final_project.data.models

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app_development_final_project.base.Callback
import com.example.app_development_final_project.base.EmptyCallback
import com.example.app_development_final_project.base.OptionalCallback
import com.example.app_development_final_project.data.entities.User
import com.example.app_development_final_project.data.services.AppLocalDb
import com.example.app_development_final_project.data.services.CloudinaryModel
import com.example.app_development_final_project.data.services.FirebaseModel
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

    private fun updateUserInRoom(user: User, callback: EmptyCallback) {
        executor.execute {
            database.UserDao().createUser(user)
        }
        callback()
    }

    fun updateUser(user: User, profilePicture: Bitmap?, callback: Callback<Pair<Boolean, String?>>) {
        createUser(user, profilePicture) { isSuccessful ->
            if (isSuccessful) {
                firebase.updateLastUpdateTimeByUser(user.id) { callback(Pair(true, null)) }
            } else {
                callback(Pair(false, "Can't update user, please try again"))
            }
        }
    }

    fun createUser(user: User, profilePicture: Bitmap?, callback: Callback<Boolean>) {
        firebase.createUser(user) { isSuccessful ->
            if (isSuccessful) {
                profilePicture?.let {
                    cloudinaryModel.uploadImage(it, user.id) { uri ->
                        var newUser = user

                        if (!uri.isNullOrBlank()) {
                            newUser = user.copy(profilePicture = uri)
                            firebase.createUser(newUser) { isSuccessful ->
                                if (isSuccessful) {
                                    updateUserInRoom(newUser) { callback(true) }
                                } else {
                                    callback(false)
                                }
                            }
                        } else {
                            updateUserInRoom(user) { callback(true) }
                        }

                        if (connectedUser?.id == user.id) {
                            connectedUser = newUser
                        }
                    }
                } ?: updateUserInRoom(user) { callback(true) }
            } else {
                callback(false)
            }
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