package com.example.app_development_final_project.adapters.feed

import androidx.recyclerview.widget.RecyclerView
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.databinding.PostFeedItemBinding

class FeedViewHolder(private val binding: PostFeedItemBinding) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var post: Post

    fun bind(post: Post) {
        this.post = post

        binding.userTextView.text = post.user.username
        binding.contentTextView.text = post.content
        binding.userRatingBar.rating = post.rating
        binding.imdbRatingBar.rating = post.movie.rating
        binding.movieNameTextView.text = post.movie.name
    }
}