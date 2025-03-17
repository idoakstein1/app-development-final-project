package com.example.app_development_final_project.data.networking

import com.example.app_development_final_project.data.entities.ImdbResponse
import com.example.app_development_final_project.data.entities.MovieDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesAPI {
    @GET("/")
    fun searchMovies(@Query("s") title: String): Call<ImdbResponse>

    @GET("/")
    fun getMovieDetails(@Query("i") imdbID: String): Call<MovieDetails?>
}