package com.example.app_development_final_project.auth

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

    fun signUp(email: String, password: String, username: String, callback: Callback<Pair<Boolean, String?>>) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                it.user?.let { firebaseUser ->
                    val user = User(firebaseUser.uid, username, email)
                    UserModel.shared.createUser(user) {
                        callback(Pair(true, null))
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
                UserModel.shared.refreshUsers()
                it.user?.uid?.let { userId ->
                    UserModel.shared.getUserById(userId) { user ->
                        UserModel.shared.connectedUser = user
                    }
                }
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

    fun signOut() {
        auth.signOut()
    }
}