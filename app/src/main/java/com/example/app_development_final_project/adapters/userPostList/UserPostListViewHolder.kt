package com.example.app_development_final_project.adapters.userPostList

import androidx.recyclerview.widget.RecyclerView
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.databinding.UserPostListItemBinding

class UserPostListViewHolder(private val binding: UserPostListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    private var post: Post? = null

    fun bind(post: Post?) {
        this.post = post
    }
}