package com.example.app_development_final_project.base

import android.app.Application
import android.content.Context

class WatchItApplication : Application() {
    companion object {
        private var instance: WatchItApplication? = null

        fun getAppContext(): Context {
            return instance?.applicationContext ?: throw IllegalStateException("Application context is missing")
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}