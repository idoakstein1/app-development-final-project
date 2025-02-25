package com.example.app_development_final_project.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    val profilePicture: String? = ""
) : Parcelable {
    companion object {
        object FieldKeys {
            const val ID = "id"
            const val USERNAME = "username"
            const val EMAIL = "email"
            const val PASSWORD = "password"
            const val PROFILE_PICTURE = "profilePicture"
        }

        fun fromJson(json: Map<*, *>): User = User(
            id = json[FieldKeys.ID] as? String ?: "",
            username = json[FieldKeys.USERNAME] as? String ?: "",
            email = json[FieldKeys.EMAIL] as? String ?: "",
            password = json[FieldKeys.PASSWORD] as? String ?: "",
            profilePicture = json[FieldKeys.PROFILE_PICTURE] as? String ?: ""
        )
    }

    val json: Map<String, String?>
        get() = hashMapOf(
            FieldKeys.ID to id,
            FieldKeys.USERNAME to username,
            FieldKeys.EMAIL to email,
            FieldKeys.PASSWORD to password,
            FieldKeys.PROFILE_PICTURE to profilePicture,
        )
}