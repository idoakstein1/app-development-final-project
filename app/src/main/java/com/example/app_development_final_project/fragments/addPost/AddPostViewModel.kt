package com.example.app_development_final_project.fragments.addPost

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_development_final_project.data.entities.ImdbResponse
import com.example.app_development_final_project.data.entities.Movie
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.data.models.PostModel
import com.example.app_development_final_project.data.models.UserModel
import com.example.app_development_final_project.data.networking.MoviesClient
import com.google.firebase.Timestamp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID
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
                this._searchResults.value = emptyList()
            } else {
                searchJob = viewModelScope.launch {
                    try {
                        MoviesClient.moviesApiClient.searchMovies(title = query).enqueue(object :
                            Callback<ImdbResponse> {
                            override fun onResponse(call: Call<ImdbResponse>, response: Response<ImdbResponse>) {
                                val results = response.body()?.search?.map { searchResult ->
                                    Movie(
                                        imdbID = searchResult.imdbID,
                                        title = searchResult.title,
                                        poster = searchResult.poster,
                                        type = searchResult.type,
                                        rating = searchResult.rating
                                    )
                                }
                                _searchResults.postValue(results?.filter { it.type !== "episode" }
                                    ?: emptyList())
                            }

                            override fun onFailure(call: Call<ImdbResponse>, t: Throwable) {
                                System.out.println("Error!")
                                System.out.println(t)
                                _searchResults.postValue(emptyList())
                            }
                        })
                    } catch (e: Exception) {
                        System.out.println("Error!")
                        System.out.println(e)
                        _searchResults.postValue(emptyList())
                    }
                }
            }
        }
    }

    fun setSelectedMovie(movie: Movie?) {
        _selectedMovie.value = movie
    }

    fun savePost(content: String, rating: Float, image: Bitmap?) {
        val post = Post(
            id = UUID.randomUUID().toString(),
            userId = UserModel.shared.connectedUser?.id ?: "",
            content = content,
            movieId = selectedMovie.value?.imdbID ?: "",
            movieTitle = selectedMovie.value?.title ?: "",
            movieRating = selectedMovie.value?.rating ?: 0f,
            rating = rating,
            photoUrl = selectedMovie.value?.poster ?: "",
            lastUpdateTime = Timestamp.now().toDate().time,
            creationTime = Timestamp.now().toDate().time,
            username = UserModel.shared.connectedUser?.username ?: "",
            userProfilePicture = UserModel.shared.connectedUser?.profilePicture ?: ""
        )

        PostModel.shared.createPost(post, image) {}
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}