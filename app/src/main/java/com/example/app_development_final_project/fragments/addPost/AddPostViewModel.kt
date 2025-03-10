package com.example.app_development_final_project.fragments.addPost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_development_final_project.data.entities.ImdbResponse
import com.example.app_development_final_project.data.entities.Movie
import com.example.app_development_final_project.data.networking.MoviesClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class AddPostViewModel : ViewModel() {
    private val _searchResults = MutableLiveData<List<Movie>>()
    val searchResults: LiveData<List<Movie>> = _searchResults

    private val _selectedMovie = MutableLiveData<Movie?>()
    val selectedMovie: LiveData<Movie?> = _selectedMovie
    private var executor = Executors.newSingleThreadExecutor()

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

                            override fun onFailure(call: Call<ImdbResponse>, t: Throwable) {
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
        _selectedMovie.value = movie
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}