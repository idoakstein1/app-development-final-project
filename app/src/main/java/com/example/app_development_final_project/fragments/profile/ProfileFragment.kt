package com.example.app_development_final_project.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.app_development_final_project.R
import com.example.app_development_final_project.adapters.userPostList.UserPostListAdapter
import com.example.app_development_final_project.auth.AuthManager
import com.example.app_development_final_project.data.models.PostModel
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

        binding?.usernameTextView?.text = viewModel.user?.username

        binding?.userPostList?.setHasFixedSize(true)
        binding?.userPostList?.layoutManager = GridLayoutManager(context, 3)

        adapter = UserPostListAdapter(viewModel.userPosts.value)
        binding?.userPostList?.adapter = adapter

        viewModel.userPosts.observe(viewLifecycleOwner) {
            adapter.update(it)
            adapter.notifyDataSetChanged()

            binding?.progressBar?.visibility = View.GONE
        }

        binding?.swipeToRefresh?.setOnRefreshListener {
            viewModel.refreshUserPosts()
        }

        PostModel.shared.loadingState.observe(viewLifecycleOwner) { state ->
            binding?.swipeToRefresh?.isRefreshing = state == PostModel.LoadingState.LOADING
            binding?.progressBar?.visibility = if (state == PostModel.LoadingState.LOADING) View.VISIBLE else View.GONE
        }

        viewModel.user?.let {
            val action = ProfileFragmentDirections.actionProfilePageFragmentToEditUserFragment(it)
            binding?.editUserButton?.setOnClickListener(Navigation.createNavigateOnClickListener(action))
        }

        return binding?.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadUser()
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.profile_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.logoutButton -> {
                        AuthManager.shared.signOut()
                        findNavController().navigate(R.id.action_profilePageFragment_to_signInFragment)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onResume() {
        super.onResume()
        getUserPosts()
    }

    private fun getUserPosts() {
        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.refreshUserPosts()
    }
}