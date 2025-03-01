package com.example.app_development_final_project.base

import android.app.Application
import android.content.Context

class WatchItApplication : Application() {
    companion object {
        private lateinit var instance: WatchItApplication

        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}