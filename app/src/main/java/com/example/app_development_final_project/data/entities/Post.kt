package com.example.app_development_final_project.data.entities

import android.content.Context
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.app_development_final_project.base.WatchItApplication
import com.example.app_development_final_project.extensions.toFirebaseTimestamp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "posts",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
    )],
)
@Parcelize
data class Post(
    @PrimaryKey val id: String,
    val userId: String,
    val username: String,
    val userProfilePicture: String? = "",
    val movieId: String,
    val movieTitle: String,
    val movieRating: Double,
    val content: String,
    val rating: Double,
    val photoUrl: String? = "",
    val lastUpdateTime: Long? = null,
    val creationTime: Long
) : Parcelable {
    object FieldKeys {
        const val ID = "id"
        const val USER_ID = "userId"
        const val USERNAME = "username"
        const val USER_PROFILE_PICTURE = "userProfilePicture"
        const val CONTENT = "content"
        const val MOVIE_ID = "movieId"
        const val MOVIE_TITLE = "movieTitle"
        const val MOVIE_RATING = "movieRating"
        const val RATING = "rating"
        const val PHOTO_URL = "photoUrl"
        const val LAST_UPDATE_TIME = "lastUpdateTime"
        const val CREATION_TIME = "creationTime"
    }

    companion object {
        private const val LOCAL_LAST_UPDATED = "localLastUpdated"

        var lastUpdated: Long
            get() = WatchItApplication.getAppContext().getSharedPreferences("TAG", Context.MODE_PRIVATE)
                ?.getLong(LOCAL_LAST_UPDATED, 0) ?: 0
            set(value) {
                WatchItApplication.getAppContext().getSharedPreferences("TAG", Context.MODE_PRIVATE)?.apply {
                    edit().putLong(LOCAL_LAST_UPDATED, value).apply()
                }
            }


        fun fromJson(json: Map<String, Any>): Post {
            val lastUpdateTime = json[FieldKeys.LAST_UPDATE_TIME] as? Timestamp
            val creationTime = json[FieldKeys.CREATION_TIME] as? Timestamp

            return Post(
                id = json[FieldKeys.ID]?.toString() ?: "",
                userId = json[FieldKeys.USER_ID]?.toString() ?: "",
                username = json[FieldKeys.USERNAME]?.toString() ?: "",
                userProfilePicture = json[FieldKeys.USER_PROFILE_PICTURE]?.toString() ?: "",
                content = json[FieldKeys.CONTENT]?.toString() ?: "",
                movieId = json[FieldKeys.MOVIE_ID]?.toString() ?: "",
                movieTitle = json[FieldKeys.MOVIE_TITLE]?.toString() ?: "",
                movieRating = (json[FieldKeys.MOVIE_RATING] as? Number)?.toDouble() ?: 0.0,
                rating = (json[FieldKeys.RATING] as? Number)?.toDouble() ?: 0.0,
                photoUrl = json[FieldKeys.PHOTO_URL]?.toString(),
                lastUpdateTime = lastUpdateTime?.toDate()?.time,
                creationTime = creationTime?.toDate()?.time ?: Timestamp.now().toDate().time,
            )
        }
    }

    val json: Map<String, Any?>
        get() = hashMapOf(
            FieldKeys.ID to id,
            FieldKeys.USER_ID to userId,
            FieldKeys.CONTENT to content,
            FieldKeys.MOVIE_ID to movieId,
            FieldKeys.MOVIE_TITLE to movieTitle,
            FieldKeys.MOVIE_RATING to movieRating,
            FieldKeys.RATING to rating,
            FieldKeys.PHOTO_URL to photoUrl,
            FieldKeys.LAST_UPDATE_TIME to FieldValue.serverTimestamp(),
            FieldKeys.CREATION_TIME to creationTime.toFirebaseTimestamp
        )
}