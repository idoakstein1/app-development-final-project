package com.example.app_development_final_project.fragments.addPost

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_development_final_project.data.AppLocalDb
import com.example.app_development_final_project.data.FirebaseModel
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.data.entities.Post.FieldKeys
import com.example.app_development_final_project.data.models.ImdbResponse
import com.example.app_development_final_project.data.models.MovieModel
import com.example.app_development_final_project.data.models.PostModel
import com.example.app_development_final_project.data.networking.MoviesClient
import com.google.firebase.Timestamp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class AddPostViewModel : ViewModel() {

    // MutableLiveData to hold the search results
    private val _searchResults = MutableLiveData<List<MovieModel>>()
    val searchResults: LiveData<List<MovieModel>> = _searchResults

    // Selected movie
    private val _selectedMovie = MutableLiveData<MovieModel?>()
    val selectedMovie: LiveData<MovieModel?> = _selectedMovie
    private var executor = Executors.newSingleThreadExecutor()

    private var firebaseModel = FirebaseModel()

    // For handling API requests
    private var searchJob: Job? = null

    // Function to search for movies
    fun searchMovies(query: String) {

        executor.execute {
            // Cancel any ongoing search
            searchJob?.cancel()

            // Don't search if query is too short
            if (query.length < 2) {
                this._searchResults.value = emptyList()
            }else {
                // Start a new search
                searchJob = viewModelScope.launch {
                    try {
                        // Show loading state if needed
                        // _isLoading.value = true

                        // Call the repository to fetch results
                        MoviesClient.moviesApiClient.searchMovies(title = query).enqueue(object :
                            Callback<ImdbResponse> {
                            override fun onResponse(call: Call<ImdbResponse>, response: Response<ImdbResponse>) {
                                val results = response.body()?.search?.map { searchResult ->
                                    MovieModel(
                                        imdbID = searchResult.imdbID,
                                        title = searchResult.title,
                                        year = searchResult.year,
                                        poster = searchResult.poster,
                                        type = searchResult.type
                                    )
                                }
                                _searchResults.postValue(results?.filter { it.type !== "episode" } ?: emptyList())  // Use `postValue` for background updates
                            }

                            override fun onFailure(call: Call<ImdbResponse>, t: Throwable) {
                                System.out.println("Error!")
                                System.out.println(t)
                                _searchResults.postValue(emptyList())
                            }
                        })
//                        val response = MoviesClient.moviesApiClient.searchMovies(query)
//                        val results = response.execute().body()?.search?.map { searchResult ->
//                            MovieModel(
//                                imdbID = searchResult.imdbID,
//                                title = searchResult.title,
//                                year = searchResult.year,
//                                poster = searchResult.poster,
//                                type = searchResult.type
//                            )
//                        }
//                        System.out.println("!!!!!!!")
//                        System.out.println(results)
//                        _searchResults.value = results ?: emptyList()

                    } catch (e: Exception) {
                        // Handle errors
                        System.out.println("Error!")
                        System.out.println(e)
                        _searchResults.postValue( emptyList())
                        // _error.value = "Failed to search: ${e.message}"
                    } finally {
                        // _isLoading.value = false
                    }
                }
            }
        }
    }

    // Set the selected movie
    fun setSelectedMovie(movie: MovieModel?) {
        _selectedMovie.value = movie
    }

    // Clear the selected movie
    fun clearSelectedMovie() {
        _selectedMovie.value = null
    }

    // Function to validate if we can save the post
    fun canSavePost(movieTitle: String, review: String, rating: Float): Boolean {
        return movieTitle.isNotBlank() && review.isNotBlank() && rating > 0
    }

    // Function to save the post
    fun savePost(movieTitle: String, review: String, rating: Float, photoUri: Uri? = null) {
        viewModelScope.launch {
            try {
                // Get the selected movie or use the title to create a basic movie object

                val post =  Post(
                    id = "generateUUID5",
                    userId = "r5ua3kGD9PTPGoaHiXVpAi49fGi1",
                    content = review,
                    movieId = selectedMovie.value?.imdbID ?: "",
                    movieTitle = selectedMovie.value?.title ?: "",
                    movieRating = -1f, //??
                    rating = rating,
                    photoUrl = selectedMovie.value?.poster ?: "",
                    lastUpdateTime = Timestamp.now().toDate().time,
                    creationTime =  Timestamp.now().toDate().time
                )

                firebaseModel.createPost(
                    post = post,
                    callback = {  }
                )
                //Looks like it dose not work
                AppLocalDb.database.PostDao().createPost(post)


//
//                // Save the post using repository
//                movieRepository.savePost(post)

                // Notify success
                // _postSaved.value = true
            } catch (e: Exception) {
                // Handle error
                // _error.value = "Failed to save post: ${e.message}"
            }
        }
    }

    // Cancel any ongoing jobs when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}