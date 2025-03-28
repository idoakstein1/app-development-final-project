package com.example.app_development_final_project.fragments.editUser

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
import com.example.app_development_final_project.data.entities.User
import com.example.app_development_final_project.data.models.UserModel
import com.example.app_development_final_project.databinding.FragmentEditUserBinding
import com.example.app_development_final_project.extensions.createTextWatcher
import com.example.app_development_final_project.extensions.formattedText
import com.example.app_development_final_project.extensions.isUsernameValid
import com.example.app_development_final_project.extensions.validateForm
import com.squareup.picasso.Picasso

class EditUserFragment : Fragment() {
    private var binding: FragmentEditUserBinding? = null

    private var user: User? = null
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var didSetProfileImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = arguments?.let {
            EditUserFragmentArgs.fromBundle(it).user
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
        binding = FragmentEditUserBinding.inflate(inflater, container, false)

        binding?.usernameTextField?.setText(user?.username)
        user?.profilePicture?.let {
            if (it.isNotBlank()) {
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.panda)
                    .into(binding?.profilePictureImageView)
            }
        }

        binding?.usernameTextField?.addTextChangedListener(createTextWatcher { validateEditUserForm() })

        binding?.saveButton?.setOnClickListener(::onSave)
        binding?.cancelButton?.setOnClickListener(::onCancel)

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                binding?.profilePictureImageView?.setImageBitmap(bitmap)
                binding?.clearImageButton?.visibility = View.VISIBLE
                didSetProfileImage = true
            }
        }

        binding?.uploadImageButton?.setOnClickListener { cameraLauncher?.launch(null) }
        binding?.clearImageButton?.setOnClickListener { resetImageView() }

        return binding?.root
    }

    private fun validateEditUserForm() {
        val username = binding?.usernameTextField?.text.formattedText

        validateForm(
            binding?.saveButton,
            user?.username != username || didSetProfileImage,
            Triple(binding?.usernameTextField, ::isUsernameValid, "Username cannot be empty"),
        )
    }

    private fun onSave(view: View) {
        val newUser = User(
            id = user?.id ?: "",
            username = binding?.usernameTextField?.text.formattedText,
            email = user?.email ?: "",
        )

        var bitmap: Bitmap? = null

        if (didSetProfileImage) {
            binding?.profilePictureImageView?.isDrawingCacheEnabled = true
            binding?.profilePictureImageView?.buildDrawingCache()
            bitmap = (binding?.profilePictureImageView?.drawable as BitmapDrawable).bitmap
        }

        binding?.progressBar?.visibility = View.VISIBLE

        UserModel.shared.updateUser(newUser, bitmap) { (isSuccessful, errorMessage) ->
            if (!isSuccessful) {
                binding?.errorLabel?.text = errorMessage
            } else {
                binding?.errorLabel?.text = null
                findNavController(view).popBackStack()
            }
            binding?.progressBar?.visibility = View.GONE
        }
    }

    private fun onCancel(view: View) {
        findNavController(view).popBackStack()
    }

    private fun resetImageView() {
        binding?.profilePictureImageView?.setImageResource(R.drawable.panda)
        binding?.clearImageButton?.visibility = View.GONE
        didSetProfileImage = false
    }
}