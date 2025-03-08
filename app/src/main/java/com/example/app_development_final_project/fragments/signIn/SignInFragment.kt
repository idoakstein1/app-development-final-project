package com.example.app_development_final_project.fragments.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.example.app_development_final_project.auth.AuthManager
import com.example.app_development_final_project.databinding.FragmentSignInBinding
import com.example.app_development_final_project.extensions.createTextWatcher
import com.example.app_development_final_project.extensions.isNotEmpty
import com.example.app_development_final_project.extensions.validateForm

class SignInFragment : Fragment() {
    private var binding: FragmentSignInBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        binding?.emailTextField?.addTextChangedListener(createTextWatcher(::validateSignInForm))
        binding?.passwordTextField?.addTextChangedListener(createTextWatcher(::validateSignInForm))

        binding?.signInButton?.setOnClickListener { onSignIn(it) }

        val signUpAction = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
        binding?.signUpLabel?.setOnClickListener(Navigation.createNavigateOnClickListener(signUpAction))

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun validateSignInForm() {
        validateForm(
            binding?.signInButton,
            null,
            Triple(binding?.emailTextField, ::isNotEmpty, null),
            Triple(binding?.passwordTextField, ::isNotEmpty, null),
        )
    }

    private fun onSignIn(view: View) {
        val email = binding?.emailTextField?.text.toString()
        val password = binding?.passwordTextField?.text.toString()

        AuthManager.shared.signIn(email, password) { (isSuccessful, errorMessage) ->
            if (!isSuccessful) {
                binding?.errorLabel?.text = errorMessage
            } else {
                binding?.errorLabel?.text = null
                findNavController(view).navigate(SignInFragmentDirections.actionSignInFragmentToFeedFragment())
            }
        }
    }
}