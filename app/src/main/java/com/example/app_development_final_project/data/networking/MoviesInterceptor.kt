package com.example.app_development_final_project.data.networking

import okhttp3.Interceptor
import okhttp3.Response
import com.example.app_development_final_project.BuildConfig

class MoviesInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newUrl = originalRequest.url().newBuilder()
            .addQueryParameter("apikey", BuildConfig.TMDB_ACCESS_TOKEN)
            .build()

        val request = originalRequest.newBuilder().url(newUrl)
            .addHeader("accept", "application/json")
            .build()

        return chain.proceed(request)
    }
}