package com.example.app_development_final_project.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.app_development_final_project.data.entities.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createUsers(users: List<User>)

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): User?

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>
}