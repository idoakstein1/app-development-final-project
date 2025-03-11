package com.example.app_development_final_project.base

import android.app.Application
import android.content.Context
import com.cloudinary.android.MediaManager
import com.cloudinary.android.policy.GlobalUploadPolicy
import com.example.app_development_final_project.BuildConfig

class WatchItApplication : Application() {
    companion object {
        private lateinit var instance: WatchItApplication
        private var isMediaManagerInitialized = false

        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        if (!isMediaManagerInitialized) {
            val config = mapOf(
                "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
                "api_key" to BuildConfig.CLOUDINARY_API_KEY,
                "api_secret" to BuildConfig.CLOUDINARY_API_SECRET
            )

            MediaManager.init(this, config)
            MediaManager.get().globalUploadPolicy = GlobalUploadPolicy.defaultPolicy()

            isMediaManagerInitialized = true
        }
    }
}