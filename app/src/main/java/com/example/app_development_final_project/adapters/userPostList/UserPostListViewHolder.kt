package com.example.app_development_final_project.adapters.userPostList

import androidx.recyclerview.widget.RecyclerView
import com.example.app_development_final_project.R
import com.example.app_development_final_project.base.OnItemClickListener
import com.example.app_development_final_project.data.entities.Post
import com.example.app_development_final_project.databinding.UserPostListItemBinding
import com.squareup.picasso.Picasso

class UserPostListViewHolder(
    private val binding: UserPostListItemBinding,
    listener: OnItemClickListener?
) : RecyclerView.ViewHolder(binding.root) {
    private var post: Post? = null

    init {
        itemView.setOnClickListener {
            listener?.onItemClick(post)
        }
    }

    fun bind(post: Post?) {
        this.post = post

        post?.photoUrl?.let {
            if (it.isNotBlank()) {
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.panda)
                    .into(binding.postPhoto)
            }
        }
    }
}