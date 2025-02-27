package com.example.app_development_final_project.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.app_development_final_project.data.entities.Post

@Dao
interface PostDao {
    @Query("SELECT * FROM posts WHERE userId <> :userId ORDER BY creationTime DESC")
    fun getFeed(userId: String): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY creationTime DESC")
    fun getPostsByUser(userId: String): LiveData<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createPost(post: Post)

    @Query("DELETE FROM posts WHERE id = :postId")
    fun deletePost(postId: String)
}