package com.example.app_development_final_project.fragments.signUp

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.example.app_development_final_project.R
import com.example.app_development_final_project.auth.AuthManager
import com.example.app_development_final_project.base.Constants
import com.example.app_development_final_project.databinding.FragmentSignUpBinding
import com.example.app_development_final_project.extensions.createTextWatcher
import com.example.app_development_final_project.extensions.formattedText
import com.example.app_development_final_project.extensions.isEmailValid
import com.example.app_development_final_project.extensions.isPasswordValid
import com.example.app_development_final_project.extensions.isUsernameValid
import com.example.app_development_final_project.extensions.validateForm


class SignUpFragment : Fragment() {
    private var binding: FragmentSignUpBinding? = null

    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var didSetProfileImage = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding?.emailTextField?.addTextChangedListener(createTextWatcher { validateSignUpForm() })
        binding?.passwordTextField?.addTextChangedListener(createTextWatcher { validateSignUpForm() })
        binding?.usernameTextField?.addTextChangedListener(createTextWatcher { validateSignUpForm() })

        val signInAction = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
        binding?.signInLabel?.setOnClickListener(Navigation.createNavigateOnClickListener(signInAction))

        binding?.signUpButton?.setOnClickListener { onSignUp(it) }

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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun validateSignUpForm() {
        validateForm(
            binding?.signUpButton,
            null,
            Triple(binding?.emailTextField, ::isEmailValid, "Invalid email format"),
            Triple(binding?.passwordTextField, ::isPasswordValid, "Password must be at least ${Constants.PASSWORD_SIZE} characters"),
            Triple(binding?.usernameTextField, ::isUsernameValid, "Username must be at least ${Constants.USERNAME_SIZE} characters")
        )
    }

    private fun onSignUp(view: View) {
        val email = binding?.emailTextField?.text.formattedText
        val password = binding?.passwordTextField?.text.formattedText
        val username = binding?.usernameTextField?.text.formattedText

        var bitmap: Bitmap? = null

        if (didSetProfileImage) {
            binding?.profilePictureImageView?.isDrawingCacheEnabled = true
            binding?.profilePictureImageView?.buildDrawingCache()
            bitmap = (binding?.profilePictureImageView?.drawable as BitmapDrawable).bitmap
        }

        binding?.progressBar?.visibility = View.VISIBLE

        AuthManager.shared.signUp(email, password, username, bitmap) { (isSuccessful, errorMessage) ->
            if (!isSuccessful) {
                binding?.errorLabel?.text = errorMessage
            } else {
                binding?.errorLabel?.text = null
                findNavController(view).navigate(SignUpFragmentDirections.actionSignUpFragmentToSignInFragment())
            }
            binding?.progressBar?.visibility = View.GONE
        }
    }

    private fun resetImageView() {
        binding?.profilePictureImageView?.setImageResource(R.drawable.panda)
        binding?.clearImageButton?.visibility = View.GONE
        didSetProfileImage = false
    }
}