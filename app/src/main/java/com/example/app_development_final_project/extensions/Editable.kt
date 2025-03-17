package com.example.app_development_final_project.extensions

import android.text.Editable

val Editable?.formattedText: String
    get() = this.toString().trim()