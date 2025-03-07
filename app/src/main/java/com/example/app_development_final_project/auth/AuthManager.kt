package com.example.app_development_final_project.auth

import com.example.app_development_final_project.base.EmptyCallback
import com.example.app_development_final_project.data.entities.User
import com.example.app_development_final_project.data.models.UserModel
import com.google.firebase.auth.FirebaseAuth

class AuthManager {
    private val auth = FirebaseAuth.getInstance()

    companion object {
        val shared = AuthManager()
    }

    fun signUp(email: String, password: String, username: String, callback: EmptyCallback) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        it.result.user?.let { firebaseUser ->
                            val user = User(firebaseUser.uid, username, email)
                            UserModel.shared.createUser(user, callback)
                        }
                    }

                    false -> callback()
                }
            }
    }

    fun signIn(email: String, password: String, callback: EmptyCallback) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                UserModel.shared.refreshUsers()
                it.user?.uid?.let { userId ->
                    UserModel.shared.getUserById(userId) { user ->
                        UserModel.shared.connectedUser = user
                    }
                }
                callback()
            }
    }

    fun signOut() {
        auth.signOut()
    }
}