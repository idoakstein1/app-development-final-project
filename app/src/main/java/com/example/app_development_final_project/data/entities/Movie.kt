package com.example.app_development_final_project.data.entities

data class Movie(
    val id: String,
    val name: String,
    val rating: Float
) {
    companion object {
        private object FieldKeys {
            const val ID = "id"
            const val NAME = "name"
            const val RATING = "rating"
        }

        fun fromJson(json: Map<*, *>): Movie = Movie(
            id = json[FieldKeys.ID] as? String ?: "",
            name = json[FieldKeys.NAME] as? String ?: "",
            rating = (json[FieldKeys.RATING] as? Number)?.toFloat() ?: 0f
        )
    }

    val json: Map<String, Any>
        get() = hashMapOf(
            FieldKeys.ID to id,
            FieldKeys.NAME to name,
            FieldKeys.RATING to rating
        )
}