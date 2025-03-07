package com.example.app_development_final_project.fragments.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.app_development_final_project.auth.AuthManager
import com.example.app_development_final_project.base.Constants
import com.example.app_development_final_project.databinding.FragmentSignUpBinding
import com.example.app_development_final_project.extensions.createTextWatcher
import com.example.app_development_final_project.extensions.getString
import com.example.app_development_final_project.extensions.isEmailValid
import com.example.app_development_final_project.extensions.isPasswordValid
import com.example.app_development_final_project.extensions.isUsernameValid
import com.example.app_development_final_project.extensions.validateForm


class SignUpFragment : Fragment() {
    private var binding: FragmentSignUpBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding?.emailTextField?.addTextChangedListener(createTextWatcher(::validateSignUpForm))
        binding?.passwordTextField?.addTextChangedListener(createTextWatcher(::validateSignUpForm))
        binding?.usernameTextField?.addTextChangedListener(createTextWatcher(::validateSignUpForm))

        val signInAction = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment2()
        binding?.signInLabel?.setOnClickListener(Navigation.createNavigateOnClickListener(signInAction))

        binding?.signUpButton?.setOnClickListener { onSignUp(it) }

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
        val email = binding?.emailTextField?.text.getString
        val password = binding?.passwordTextField?.text.getString
        val username = binding?.usernameTextField?.text.getString

        AuthManager.shared.signUp(email, password, username) {
            val singInAction = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment2()
            Navigation.findNavController(view).navigate(singInAction)
        }
    }
}