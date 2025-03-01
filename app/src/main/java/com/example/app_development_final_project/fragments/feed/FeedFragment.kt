package com.example.app_development_final_project.fragments.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_development_final_project.adapters.feed.FeedAdapter
import com.example.app_development_final_project.data.models.PostModel
import com.example.app_development_final_project.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {
    private lateinit var adapter: FeedAdapter
    private var binding: FragmentFeedBinding? = null

    private val viewModel: FeedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(inflater, container, false)

        binding?.postList?.setHasFixedSize(true)
        binding?.postList?.layoutManager = LinearLayoutManager(context)

        adapter = FeedAdapter(viewModel.posts.value)
        binding?.postList?.adapter = adapter

        viewModel.posts.observe(viewLifecycleOwner) {
            adapter.update(it)
            adapter.notifyDataSetChanged()

            binding?.progressBar?.visibility = View.GONE
        }

        binding?.swipeToRefresh?.setOnRefreshListener {
            viewModel.refreshFeed()
        }

        PostModel.shared.loadingState.observe(viewLifecycleOwner) { state ->
            binding?.swipeToRefresh?.isRefreshing = state == PostModel.LoadingState.LOADING
            binding?.progressBar?.visibility = if (state == PostModel.LoadingState.LOADING) View.VISIBLE else View.GONE
        }

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onResume() {
        super.onResume()
        getFeed()
    }

    private fun getFeed() {
        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.refreshFeed()
    }
}