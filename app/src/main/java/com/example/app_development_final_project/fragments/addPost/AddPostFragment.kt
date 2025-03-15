package com.example.app_development_final_project.fragments.addPost

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.app_development_final_project.R
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.data.models.PostModel
import com.example.app_development_final_project.data.models.UserModel
import com.example.app_development_final_project.databinding.FragmentAddPostBinding
import com.example.app_development_final_project.extensions.createTextWatcher
import com.example.app_development_final_project.extensions.getString
import com.example.app_development_final_project.extensions.isNotEmpty
import com.example.app_development_final_project.extensions.validateForm
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class AddPostFragment : Fragment() {
    var binding: FragmentAddPostBinding? = null
    private val viewModel: AddPostViewModel by viewModels()

    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var didSetProfileImage = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPostBinding.inflate(inflater, container, false)

        val movieAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        )
        binding?.movieTextField?.setAdapter(movieAdapter)

        binding?.ratingBar?.setOnRatingBarChangeListener { _, _, _ -> validateAddPostForm() }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                binding?.photoImageView?.setImageBitmap(bitmap)
                binding?.clearImageButton?.visibility = View.VISIBLE
                didSetProfileImage = true
            }
        }

        binding?.uploadImageButton?.setOnClickListener { cameraLauncher?.launch(null) }
        binding?.clearImageButton?.setOnClickListener { resetImageView() }

        binding?.movieTextField?.addTextChangedListener(object : TextWatcher {
            private var searchJob: Job? = null

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                viewModel.setSelectedMovie(null)
                validateAddPostForm()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

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

        binding?.saveButton?.setOnClickListener { onSave() }

        viewModel.searchResults.observe(viewLifecycleOwner) { movies ->
            movieAdapter.clear()
            movieAdapter.addAll(movies.map { it.title })
            movieAdapter.notifyDataSetChanged()
        }

        binding?.movieTextField?.setOnItemClickListener { parent, _, position, _ ->
            val selectedMovieTitle = parent.getItemAtPosition(position) as String
            val selectedMovie = viewModel.searchResults.value?.find { it.title == selectedMovieTitle }
            viewModel.setSelectedMovie(selectedMovie)
            validateAddPostForm()
        }

        binding?.contentTextField?.addTextChangedListener(createTextWatcher(::validateAddPostForm))

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun validateAddPostForm() {
        validateForm(
            binding?.saveButton,
            null,
            Triple(binding?.contentTextField, ::isNotEmpty, "Content cannot be empty"),
            Triple(binding?.movieTextField, ::isNotEmpty, "Movie/show cannot be empty")
        )
    }

    private fun onSave() {
        val selectedMovie = viewModel.selectedMovie.value
        val content = binding?.contentTextField?.text.getString
        val rating = binding?.ratingBar?.rating?.toDouble() ?: 0.0

        var bitmap: Bitmap? = null

        if (didSetProfileImage) {
            binding?.photoImageView?.isDrawingCacheEnabled = true
            binding?.photoImageView?.buildDrawingCache()
            bitmap = (binding?.photoImageView?.drawable as BitmapDrawable).bitmap
        }

        val post = Post(
            id = UUID.randomUUID().toString(),
            userId = UserModel.shared.connectedUser?.id ?: "",
            content = content,
            movieId = selectedMovie?.imdbID ?: "",
            movieTitle = selectedMovie?.title ?: "",
            movieRating = selectedMovie?.imdbRating?.toDouble() ?: 0.0,
            rating = rating,
            photoUrl = selectedMovie?.poster ?: "",
            lastUpdateTime = Timestamp.now().toDate().time,
            creationTime = Timestamp.now().toDate().time,
            username = UserModel.shared.connectedUser?.username ?: "",
            userProfilePicture = UserModel.shared.connectedUser?.profilePicture ?: ""
        )

        PostModel.shared.createPost(post, bitmap) {
            resetForm()
        }
    }

    private fun resetForm() {
        binding?.movieTextField?.text?.clear()
        binding?.contentTextField?.text?.clear()
        binding?.ratingBar?.rating = 0f
        resetImageView()
    }

    private fun resetImageView() {
        binding?.photoImageView?.setImageResource(R.drawable.panda)
        binding?.clearImageButton?.visibility = View.GONE
        didSetProfileImage = false
    }
}
