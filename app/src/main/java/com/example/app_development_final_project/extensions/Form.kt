package com.example.app_development_final_project.extensions

import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import com.example.app_development_final_project.base.Constants

fun createTextWatcher(onTextChanged: (s: CharSequence?) -> Unit) = object : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        onTextChanged(s)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}

fun validateForm(button: Button?, extraCondition: Boolean?, vararg fields: Triple<EditText?, (value: String) -> Boolean, String?>) {
    var isFormValid = true

    for ((field, validator, errorMessage) in fields) {
        val value = field?.text.formattedText

        field?.error = null

        if (!validator(value)) {
            field?.error = errorMessage
            isFormValid = false
        }
    }

    if (extraCondition != null && !extraCondition) {
        isFormValid = false
    }

    button?.isEnabled = isFormValid
}

fun isNotEmpty(value: String): Boolean = value.isNotEmpty()

fun isEmailValid(email: String): Boolean = isNotEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()

fun isUsernameValid(username: String): Boolean = isNotEmpty(username) && username.length >= Constants.USERNAME_SIZE

fun isPasswordValid(password: String): Boolean = isNotEmpty(password) && password.length >= Constants.PASSWORD_SIZE
