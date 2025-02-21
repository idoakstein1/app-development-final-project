package com.example.app_development_final_project.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.app_development_final_project.adapters.userPostList.UserPostListAdapter
import com.example.app_development_final_project.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var adapter: UserPostListAdapter
    private var binding: FragmentProfileBinding? = null

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding?.userPostList?.setHasFixedSize(true)
        binding?.userPostList?.layoutManager = GridLayoutManager(context, 3)

        adapter = UserPostListAdapter(viewModel.userPosts)
        binding?.userPostList?.adapter = adapter

        val action = ProfileFragmentDirections.actionProfilePageFragmentToEditUserFragment(viewModel.user)
        binding?.editUserButton?.setOnClickListener(Navigation.createNavigateOnClickListener(action))

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}