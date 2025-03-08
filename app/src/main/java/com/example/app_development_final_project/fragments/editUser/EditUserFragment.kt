package com.example.app_development_final_project.fragments.editUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.example.app_development_final_project.data.entities.User
import com.example.app_development_final_project.data.models.UserModel
import com.example.app_development_final_project.databinding.FragmentEditUserBinding
import com.example.app_development_final_project.extensions.createTextWatcher
import com.example.app_development_final_project.extensions.getString
import com.example.app_development_final_project.extensions.isEmailValid
import com.example.app_development_final_project.extensions.isUsernameValid
import com.example.app_development_final_project.extensions.validateForm

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

        binding?.usernameTextField?.addTextChangedListener(createTextWatcher(::validateEditUserForm))
        binding?.emailTextField?.addTextChangedListener(createTextWatcher(::validateEditUserForm))

        binding?.saveButton?.setOnClickListener(::onSave)
        binding?.cancelButton?.setOnClickListener(::onCancel)

        return binding?.root
    }

    private fun validateEditUserForm() {
        val username = binding?.usernameTextField?.text.getString
        val email = binding?.emailTextField?.text.getString

        validateForm(
            binding?.saveButton,
            user?.username != username || user?.email != email,
            Triple(binding?.usernameTextField, ::isUsernameValid, "Username cannot be empty"),
            Triple(binding?.emailTextField, ::isEmailValid, "Invalid email format"),
        )
    }

    private fun onSave(view: View) {
        val newUser = User(
            id = user?.id ?: "",
            username = binding?.usernameTextField?.text.getString,
            email = binding?.emailTextField?.text.getString,
        )

        UserModel.shared.updateUser(newUser) {
            findNavController(view).popBackStack()
        }
    }

    private fun onCancel(view: View) {
        findNavController(view).popBackStack()
    }
}