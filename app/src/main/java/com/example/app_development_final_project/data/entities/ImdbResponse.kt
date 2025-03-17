package com.example.app_development_final_project.data.entities

import com.google.gson.annotations.SerializedName

data class ImdbResponse(
    @SerializedName("Search") val search: List<Movie>?
)
