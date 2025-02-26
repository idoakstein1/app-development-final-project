package com.example.app_development_final_project.adapters.feed

import androidx.recyclerview.widget.RecyclerView
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.databinding.PostFeedItemBinding

class FeedViewHolder(private val binding: PostFeedItemBinding) : RecyclerView.ViewHolder(binding.root) {
    private var post: Post? = null

    fun bind(post: Post?) {
        this.post = post

        binding.userTextView.text = post?.userId
        binding.contentTextView.text = post?.content
        binding.userRatingBar.rating = post?.rating ?: 0f
        binding.imdbRatingBar.rating = post?.movieRating ?: 0f
        binding.movieNameTextView.text = post?.movieTitle
    }
}