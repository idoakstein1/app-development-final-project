package com.example.app_development_final_project.adapters.userPostList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateMargins
import androidx.recyclerview.widget.RecyclerView
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.databinding.UserPostListItemBinding

class UserPostListAdapter(private var posts: List<Post>) : RecyclerView.Adapter<UserPostListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPostListViewHolder {
        val inflation = LayoutInflater.from(parent.context)
        val binding = UserPostListItemBinding.inflate(inflation, parent, false)

        val layoutParams = binding.root.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.updateMargins(left = 10, top = 10, right = 10, bottom = 10)
        binding.root.layoutParams = layoutParams

        return UserPostListViewHolder(binding)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: UserPostListViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    fun update(posts: List<Post>) {
        this.posts = posts
    }
}