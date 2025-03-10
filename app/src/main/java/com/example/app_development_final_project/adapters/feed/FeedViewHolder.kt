package com.example.app_development_final_project.adapters.feed

import androidx.recyclerview.widget.RecyclerView
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.databinding.PostFeedItemBinding

class FeedViewHolder(private val binding: PostFeedItemBinding) : RecyclerView.ViewHolder(binding.root) {
    private var post: Post? = null

    fun bind(post: Post?) {
        this.post = post

        binding.userTextView.text = post?.username
        binding.contentTextView.text = post?.content
        binding.userRatingBar.rating = post?.rating ?: 0f
        binding.userRatingLabel.text = "User rating: ${post?.rating ?: 0}/5"
        binding.imdbRatingBar.rating = post?.movieRating ?: 0f
        binding.imdbRatingLabel.text = "IMDB rating: ${post?.movieRating ?: 0}/10"
        binding.movieNameTextView.text = post?.movieTitle
    }
}