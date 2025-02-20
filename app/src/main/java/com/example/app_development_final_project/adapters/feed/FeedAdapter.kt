package com.example.app_development_final_project.adapters.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateMargins
import androidx.recyclerview.widget.RecyclerView
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.databinding.PostFeedItemBinding

class FeedAdapter(private var posts: List<Post>) : RecyclerView.Adapter<FeedViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val inflation = LayoutInflater.from(parent.context)
        val binding = PostFeedItemBinding.inflate(inflation, parent, false)

        val layoutParams = binding.root.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.updateMargins(top = 50, bottom = 50)
        binding.root.layoutParams = layoutParams

        return FeedViewHolder(binding)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(posts[position])
    }
}