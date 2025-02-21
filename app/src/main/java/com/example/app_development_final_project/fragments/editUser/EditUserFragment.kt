package com.example.app_development_final_project.fragments.editUser

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.app_development_final_project.data.entities.User
import com.example.app_development_final_project.databinding.FragmentEditUserBinding

class EditUserFragment : Fragment() {
    private var binding: FragmentEditUserBinding? = null
    private var user: User? = null

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
        binding?.emailTextField?.setText(user?.email)

        binding?.usernameTextField?.addTextChangedListener(formTextWatcher)
        binding?.emailTextField?.addTextChangedListener(formTextWatcher)

        binding?.saveButton?.setOnClickListener(::onSave)
        binding?.cancelButton?.setOnClickListener(::onCancel)

        return binding?.root
    }

    private val formTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            validateForm()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun validateForm() {
        val username = binding?.usernameTextField?.text.toString().trim()
        val email = binding?.emailTextField?.text.toString().trim()

        val isUsernameNotEmpty = username.isNotEmpty()
        val isEmailValid = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isDifferentFromCurrent = user?.username != username || user?.email != email

        binding?.usernameTextField?.error = null
        binding?.emailTextField?.error = null

        var isFormValid = true

        if (!isUsernameNotEmpty) {
            binding?.usernameTextField?.error = "Username cannot be empty"
            isFormValid = false
        }

        if (!isEmailValid) {
            binding?.emailTextField?.error = if (email.isEmpty()) "Email cannot be empty" else "Invalid email format"
            isFormValid = false
        }

        if (!isDifferentFromCurrent) {
            isFormValid = false
        }

        binding?.saveButton?.isEnabled = isFormValid
    }

    private fun onSave(view: View) {}

    private fun onCancel(view: View) {
        Navigation.findNavController(view).popBackStack()
    }
}