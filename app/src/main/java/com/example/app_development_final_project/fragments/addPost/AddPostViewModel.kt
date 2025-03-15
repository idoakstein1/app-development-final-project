package com.example.app_development_final_project.fragments.addPost

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app_development_final_project.data.entities.ImdbResponse
import com.example.app_development_final_project.data.entities.Movie
import com.example.app_development_final_project.data.entities.MovieDetails
import com.example.app_development_final_project.data.networking.MoviesClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPostViewModel : ViewModel() {
    private val _searchResults = MutableLiveData<List<Movie>>()
    val searchResults: LiveData<List<Movie>> = _searchResults

    private val _selectedMovie = MutableLiveData<MovieDetails?>()
    val selectedMovie: LiveData<MovieDetails?> = _selectedMovie

    private val debounceHandler = Handler(Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null

    fun searchMovies(query: String) {
        if (query.length < 2) {
            _searchResults.postValue(emptyList())
            return
        }

        debounceRunnable?.let { debounceHandler.removeCallbacks(it) }

        debounceRunnable = Runnable {
            try {
                MoviesClient.moviesApiClient.searchMovies(title = query).enqueue(object : Callback<ImdbResponse> {
                    override fun onResponse(call: Call<ImdbResponse>, response: Response<ImdbResponse>) {
                        _searchResults.postValue(response.body()?.search?.filter { it.type != "episode" } ?: emptyList())
                    }

                    override fun onFailure(call: Call<ImdbResponse>, error: Throwable) {
                        _searchResults.postValue(emptyList())
                    }
                })
            } catch (e: Exception) {
                _searchResults.postValue(emptyList())
            }
        }

        debounceHandler.postDelayed(debounceRunnable!!, 300)
    }

    fun setSelectedMovie(movie: Movie?) {
        movie?.let {
            try {
                MoviesClient.moviesApiClient.getMovieDetails(imdbID = it.imdbID).enqueue(object : Callback<MovieDetails?> {
                    override fun onResponse(call: Call<MovieDetails?>, response: Response<MovieDetails?>) {
                        _selectedMovie.value = response.body()
                    }

                    override fun onFailure(call: Call<MovieDetails?>, error: Throwable) {
                        _selectedMovie.value = null
                    }
                })
            } catch (e: Exception) {
                _selectedMovie.value = null
            }
        }
    }
}