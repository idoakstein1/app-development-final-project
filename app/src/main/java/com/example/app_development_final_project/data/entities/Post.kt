package com.example.app_development_final_project.data.entities

import android.content.Context
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.app_development_final_project.base.WatchItApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

@Entity(
    tableName = "posts",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
    )],
)
data class Post(
    @PrimaryKey val id: String,
    val userId: String,
    val content: String,
    val movieId: String,
    val movieTitle: String,
    val movieRating: Float,
    val rating: Float,
    val photoUrl: String? = "",
    val lastUpdateTime: Long? = null,
    val creationTime: Long? = null
) {
    object FieldKeys {
        const val ID = "id"
        const val USER_ID = "userId"
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
        private const val LOCAL_LAST_UPDATED = "localStudentLastUpdated"

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
                content = json[FieldKeys.CONTENT]?.toString() ?: "",
                movieId = json[FieldKeys.MOVIE_ID]?.toString() ?: "",
                movieTitle = json[FieldKeys.MOVIE_TITLE]?.toString() ?: "",
                movieRating = (json[FieldKeys.MOVIE_RATING] as? Number)?.toFloat() ?: 0f,
                rating = (json[FieldKeys.RATING] as? Number)?.toFloat() ?: 0f,
                photoUrl = json[FieldKeys.PHOTO_URL]?.toString(),
                lastUpdateTime = lastUpdateTime?.toDate()?.time,
                creationTime = creationTime?.toDate()?.time
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
            FieldKeys.CREATION_TIME to creationTime
        )
}