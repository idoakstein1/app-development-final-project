package com.example.app_development_final_project.data.entities

data class Post(
    val id: String,
    val user: User,
    val content: String,
    val movie: Movie,
    val rating: Float,
    val photoUrl: String? = "",
) {
    companion object {
        object FieldKeys {
            const val ID = "id"
            const val USER = "user"
            const val CONTENT = "content"
            const val MOVIE = "movie"
            const val RATING = "rating"
            const val PHOTO_URL = "photoUrl"
        }

        fun fromJson(json: Map<String, Any>): Post {
            val userJson = json[FieldKeys.USER] as? Map<*, *> ?: throw IllegalArgumentException("User is missing")
            val movieJson = json[FieldKeys.MOVIE] as? Map<*, *> ?: throw IllegalArgumentException("Movie is missing")

            return Post(
                id = json[FieldKeys.ID]?.toString() ?: "",
                user = User.fromJson(userJson),
                content = json[FieldKeys.CONTENT]?.toString() ?: "",
                movie = Movie.fromJson(movieJson),
                rating = (json[FieldKeys.RATING] as? Number)?.toFloat() ?: 0f,
                photoUrl = json[FieldKeys.PHOTO_URL]?.toString()
            )
        }
    }

    val json: Map<String, Any>
        get() = hashMapOf(
            FieldKeys.ID to id,
            FieldKeys.USER to user.json,
            FieldKeys.CONTENT to content,
            FieldKeys.MOVIE to movie.json,
            FieldKeys.RATING to rating,
        )
}