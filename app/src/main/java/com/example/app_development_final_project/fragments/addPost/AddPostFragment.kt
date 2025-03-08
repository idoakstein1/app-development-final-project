package com.example.app_development_final_project.fragments.addPost

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.fragment.app.viewModels
import com.example.app_development_final_project.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddPostFragment : Fragment() {

    private val viewModel: AddPostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val movieTextField = view.findViewById<AutoCompleteTextView>(R.id.movieTextField)
        val reviewTextField = view.findViewById<EditText>(R.id.reviewTextField)
        val saveButton = view.findViewById<Button>(R.id.saveButton)

        val movieAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        )

        movieTextField.setAdapter(movieAdapter)

        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (fromUser) {
                updateSaveButtonState(reviewTextField, ratingBar, saveButton)
            }
        }

        movieTextField.addTextChangedListener(object : TextWatcher {
            // Use a debounce mechanism to avoid too many API calls
            private var searchJob: Job? = null

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                viewModel.setSelectedMovie(null)
                updateSaveButtonState( reviewTextField, ratingBar, saveButton)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                searchJob?.cancel()
//
//                val query = s.toString().trim()
//                if (query.length >= 2) {
//                    searchJob = CoroutineScope(Dispatchers.IO).launch {
//                        delay(300)
//                        viewModel.searchMovies(query)
//                    }
//                }
            }

            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()

                val query = s.toString().trim()
                if (query.length >= 2) {
                    searchJob = CoroutineScope(Dispatchers.IO).launch {
                        delay(300)
                        viewModel.searchMovies(query)
                    }
                }
            }
        })

        saveButton.setOnClickListener({
            val selectedMovie = viewModel.selectedMovie.value?.title
            val reviewText = reviewTextField.text.toString().trim()
            val rating = ratingBar.rating
            if (selectedMovie != null && reviewText.isNotEmpty()) {
                viewModel.savePost(selectedMovie, reviewText, rating)
                movieTextField.text.clear()
                reviewTextField.text.clear()
                ratingBar.rating = 0f
                updateSaveButtonState(reviewTextField, ratingBar, saveButton)
            }
        })

        viewModel.searchResults.observe(viewLifecycleOwner) { movies ->
            movieAdapter.clear()
            movieAdapter.addAll(movies.map { it.title })
            movieAdapter.notifyDataSetChanged()
        }

        movieTextField.setOnItemClickListener { parent, _, position, _ ->
            val selectedMovieTitle = parent.getItemAtPosition(position) as String
            val selectedMovie = viewModel.searchResults.value?.find { it.title == selectedMovieTitle }
            viewModel.setSelectedMovie(selectedMovie)
            updateSaveButtonState(reviewTextField, ratingBar, saveButton)
        }

        reviewTextField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateSaveButtonState(reviewTextField, ratingBar, saveButton)
            }
        })
    }

    private fun updateSaveButtonState(
        reviewTextField: EditText,
        ratingBar: RatingBar,
        saveButton: Button
    ) {
        val reviewText = reviewTextField.text.toString().trim()
        val rating = ratingBar.rating

        saveButton.isEnabled = viewModel.selectedMovie.value != null  &&
                reviewText.isNotEmpty() &&
                rating >= 0
    }
}
