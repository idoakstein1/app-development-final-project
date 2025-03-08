package com.example.app_development_final_project.services

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.app_development_final_project.base.WatchItApplication
import com.example.app_development_final_project.data.dao.PostDao
import com.example.app_development_final_project.data.dao.UserDao
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.data.entities.User

@Database(entities = [Post::class, User::class], version = 9)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun PostDao(): PostDao
    abstract fun UserDao(): UserDao
}

object AppLocalDb {
    val database: AppLocalDbRepository by lazy {
        Room
            .databaseBuilder(
                context = WatchItApplication.getAppContext(),
                klass = AppLocalDbRepository::class.java,
                name = "dbFileName.db"
            )
            .fallbackToDestructiveMigration()
            .build()
    }
}