package com.example.app_development_final_project.extensions

import android.text.Editable

val Editable?.getString: String
    get() = this.toString().trim()