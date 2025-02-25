package com.example.app_development_final_project.base

typealias EmptyCallback = () -> Unit
typealias ListCallback<T> = (List<T>) -> Unit
typealias Callback<T> = (T) -> Unit
typealias OptionalCallback<T> = (T?) -> Unit

object Constants {
    object Collections {
        const val POSTS = "posts"
        const val USERS = "users"
    }
}