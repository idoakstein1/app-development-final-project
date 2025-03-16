package com.example.app_development_final_project.auth

import android.graphics.Bitmap
import com.example.app_development_final_project.base.Callback
import com.example.app_development_final_project.data.entities.User
import com.example.app_development_final_project.data.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class AuthManager {
    private val auth = FirebaseAuth.getInstance()

    companion object {
        val shared = AuthManager()
    }

    fun signUp(email: String, password: String, username: String, profilePicture: Bitmap?, callback: Callback<Pair<Boolean, String?>>) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                it.user?.let { firebaseUser ->
                    val user = User(firebaseUser.uid, username, email)
                    UserModel.shared.createUser(user, profilePicture) { isSuccessful ->
                        callback(Pair(isSuccessful, if (isSuccessful) null else "Sign up failed. Please try again"))
                    }
                }
            }
            .addOnFailureListener {
                callback(Pair(false, "Sign up failed. Please try again"))
            }
    }

    fun signIn(email: String, password: String, callback: Callback<Pair<Boolean, String?>>) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                connectUser(it.user?.uid ?: "")
                callback(Pair(true, null))
            }
            .addOnFailureListener { exception ->
                val errorMessage = when (exception) {
                    is FirebaseAuthInvalidUserException -> "Invalid email or password"
                    is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
                    else -> "Sign in failed. Please try again"
                }
                callback(Pair(false, errorMessage))
            }

    }

    fun connectUser(userId: String) {
        UserModel.shared.refreshUsers()
        UserModel.shared.getUserById(userId) { user ->
            UserModel.shared.connectedUser = user
        }
    }

    fun getCurrentUser(): String? {
        return auth.uid
    }

    fun signOut() {
        auth.signOut()
    }
}