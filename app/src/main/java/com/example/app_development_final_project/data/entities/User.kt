package com.example.app_development_final_project.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "users")
@Parcelize
data class User(
    @PrimaryKey val id: String,
    val username: String,
    val email: String,
    val profilePicture: String? = ""
) : Parcelable {
    object FieldKeys {
        const val ID = "id"
        const val USERNAME = "username"
        const val EMAIL = "email"
        const val PROFILE_PICTURE = "profilePicture"
    }

    companion object {
        fun fromJson(json: Map<String, Any>): User = User(
            id = json[FieldKeys.ID] as? String ?: "",
            username = json[FieldKeys.USERNAME] as? String ?: "",
            email = json[FieldKeys.EMAIL] as? String ?: "",
            profilePicture = json[FieldKeys.PROFILE_PICTURE] as? String ?: ""
        )
    }

    val json: Map<String, String?>
        get() = hashMapOf(
            FieldKeys.ID to id,
            FieldKeys.USERNAME to username,
            FieldKeys.EMAIL to email,
            FieldKeys.PROFILE_PICTURE to profilePicture,
        )
}