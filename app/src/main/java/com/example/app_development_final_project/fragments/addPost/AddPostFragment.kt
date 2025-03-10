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
import com.example.app_development_final_project.databinding.FragmentAddPostBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

        binding?.ratingBar?.setOnRatingBarChangeListener { _, _, fromUser ->
            if (fromUser) {
                updateSaveButtonState()
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            binding?.photoImageView?.setImageBitmap(bitmap)
            didSetProfileImage = true
        }

        binding?.uploadImageButton?.setOnClickListener {
            cameraLauncher?.launch(null)
        }

        binding?.movieTextField?.addTextChangedListener(object : TextWatcher {
            private var searchJob: Job? = null

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                viewModel.setSelectedMovie(null)
                updateSaveButtonState()
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

        binding?.saveButton?.setOnClickListener({
            val selectedMovie = viewModel.selectedMovie.value?.title
            val content = binding?.contentTextField?.text.toString().trim()
            val rating = binding?.ratingBar?.rating ?: 0f

            var bitmap: Bitmap? = null

            if (didSetProfileImage) {
                binding?.photoImageView?.isDrawingCacheEnabled = true
                binding?.photoImageView?.buildDrawingCache()
                bitmap = (binding?.photoImageView?.drawable as BitmapDrawable).bitmap
            }

            if (selectedMovie != null && content.isNotEmpty()) {
                viewModel.savePost(content, rating, bitmap)
                binding?.movieTextField?.text?.clear()
                binding?.contentTextField?.text?.clear()
                binding?.ratingBar?.rating = 0f
                updateSaveButtonState()
            }
        })

        viewModel.searchResults.observe(viewLifecycleOwner) { movies ->
            movieAdapter.clear()
            movieAdapter.addAll(movies.map { it.title })
            movieAdapter.notifyDataSetChanged()
        }

        binding?.movieTextField?.setOnItemClickListener { parent, _, position, _ ->
            val selectedMovieTitle = parent.getItemAtPosition(position) as String
            val selectedMovie = viewModel.searchResults.value?.find { it.title == selectedMovieTitle }
            viewModel.setSelectedMovie(selectedMovie)
            updateSaveButtonState()
        }

        binding?.contentTextField?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateSaveButtonState()
            }
        })

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun updateSaveButtonState() {
        val content = binding?.contentTextField?.text.toString().trim()
        val rating = binding?.ratingBar?.rating ?: 0f

        binding?.saveButton?.isEnabled = viewModel.selectedMovie.value != null &&
                content.isNotEmpty() &&
                rating >= 0
    }
}
