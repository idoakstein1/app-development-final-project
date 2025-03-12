package com.example.app_development_final_project.fragments.editPost

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.example.app_development_final_project.R
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.data.models.PostModel
import com.example.app_development_final_project.databinding.FragmentEditPostBinding
import com.example.app_development_final_project.extensions.createTextWatcher
import com.example.app_development_final_project.extensions.getString
import com.example.app_development_final_project.extensions.isNotEmpty
import com.example.app_development_final_project.extensions.validateForm
import com.squareup.picasso.Picasso

class EditPostFragment : Fragment() {
    var binding: FragmentEditPostBinding? = null

    private var post: Post? = null
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var didSetProfileImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        post = arguments?.let {
            EditPostFragmentArgs.fromBundle(it).post
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPostBinding.inflate(inflater, container, false)

        binding?.contentTextField?.setText(post?.content)
        binding?.ratingBar?.rating = post?.rating ?: 0f
        post?.photoUrl?.let {
            if (it.isNotBlank()) {
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.panda)
                    .into(binding?.photoImageView)
            }
        }

        binding?.contentTextField?.addTextChangedListener(createTextWatcher(::validateEditPostForm))
        binding?.ratingBar?.setOnRatingBarChangeListener { _, _, _ -> validateEditPostForm() }

        binding?.saveButton?.setOnClickListener(::onSave)
        binding?.cancelButton?.setOnClickListener(::onCancel)
        binding?.deletePostButton?.setOnClickListener(::onDelete)

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            binding?.photoImageView?.setImageBitmap(bitmap)
            didSetProfileImage = true
        }

        binding?.uploadImageButton?.setOnClickListener {
            cameraLauncher?.launch(null)
        }


        return binding?.root
    }

    private fun validateEditPostForm() {
        val content = binding?.contentTextField?.text.getString
        val rating = binding?.ratingBar?.rating

        validateForm(
            binding?.saveButton,
            post?.content != content || didSetProfileImage || post?.rating != rating,
            Triple(binding?.contentTextField, ::isNotEmpty, "Username cannot be empty"),
        )
    }

    private fun onSave(view: View) {
        val newPost = post?.copy(
            content = binding?.contentTextField?.text.getString,
            rating = binding?.ratingBar?.rating ?: 0f
        )

        var bitmap: Bitmap? = null

        if (didSetProfileImage) {
            binding?.photoImageView?.isDrawingCacheEnabled = true
            binding?.photoImageView?.buildDrawingCache()
            bitmap = (binding?.photoImageView?.drawable as BitmapDrawable).bitmap
        }

        newPost?.let {
            PostModel.shared.createPost(it, bitmap) {
                findNavController(view).popBackStack()
            }
        }
    }

    private fun onCancel(view: View) {
        findNavController(view).popBackStack()
    }

    private fun onDelete(view: View) {
        post?.let {
            PostModel.shared.deletePost(it.id) {
                findNavController(view).popBackStack()
            }
        }
    }
}