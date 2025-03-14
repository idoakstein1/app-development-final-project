package com.example.app_development_final_project.fragments.addPost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_development_final_project.data.entities.ImdbResponse
import com.example.app_development_final_project.data.entities.Movie
import com.example.app_development_final_project.data.entities.MovieDetails
import com.example.app_development_final_project.data.networking.MoviesClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class AddPostViewModel : ViewModel() {
    private var executor = Executors.newSingleThreadExecutor()

    private val _searchResults = MutableLiveData<List<Movie>>()
    val searchResults: LiveData<List<Movie>> = _searchResults

    private val _selectedMovie = MutableLiveData<MovieDetails?>()
    val selectedMovie: LiveData<MovieDetails?> = _selectedMovie

    private var searchJob: Job? = null

    fun searchMovies(query: String) {
        executor.execute {
            searchJob?.cancel()

            if (query.length < 2) {
                _searchResults.value = emptyList()
            } else {
                searchJob = viewModelScope.launch {
                    try {
                        MoviesClient.moviesApiClient.searchMovies(title = query).enqueue(object : Callback<ImdbResponse> {
                            override fun onResponse(call: Call<ImdbResponse>, response: Response<ImdbResponse>) {
                                val movies = response.body()?.search?.filter { it.type !== "episode" } ?: emptyList()
                                _searchResults.postValue(movies)
                            }

                            override fun onFailure(call: Call<ImdbResponse>, error: Throwable) {
                                _searchResults.postValue(emptyList())
                            }
                        })
                    } catch (e: Exception) {
                        _searchResults.postValue(emptyList())
                    }
                }
            }
        }
    }

    fun setSelectedMovie(movie: Movie?) {
        movie?.let {
            executor.execute {
                try {
                    MoviesClient.moviesApiClient.getMovieDetails(imdbID = it.imdbID).enqueue(object : Callback<MovieDetails?> {
                        override fun onResponse(call: Call<MovieDetails?>, response: Response<MovieDetails?>) {
                            _selectedMovie.postValue(response.body())
                        }

                        override fun onFailure(call: Call<MovieDetails?>, error: Throwable) {
                            _selectedMovie.postValue(null)
                        }
                    })
                } catch (e: Exception) {
                    _selectedMovie.postValue(null)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}