package com.example.app_development_final_project.data.entities

import com.google.gson.annotations.SerializedName

data class ImdbResponse(
    @SerializedName("Response") val response: String,
    @SerializedName("Search") val search: List<Movie>?
)
