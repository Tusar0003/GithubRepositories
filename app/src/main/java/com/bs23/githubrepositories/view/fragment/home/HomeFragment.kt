package com.bs23.githubrepositories.view.fragment.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bs23.githubrepositories.R
import com.bs23.githubrepositories.databinding.FragmentHomeBinding
import com.bs23.githubrepositories.utils.*
import com.bs23.githubrepositories.view.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()

    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        launchAndRepeatWithViewLifecycle {
            launch {
                viewModel.message.collect {
                    showDialog {
                        setMessage(it)
                        positiveButton(getString(R.string.ok)) {}
                    }
                }
            }

            launch {
                binding.toolbar.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_star -> viewModel.sortByStar()
                        R.id.menu_date -> viewModel.sortByDate()
                    }

                    return@setOnMenuItemClickListener true
                }
            }

            launch {
                binding.swipeRefreshLayout.setOnChildScrollUpCallback(object : SwipeRefreshLayout.OnChildScrollUpCallback {
                    override fun canChildScrollUp(parent: SwipeRefreshLayout, child: View?): Boolean {
                        return binding.repoRecyclerView.canScrollVertically(-1)
                    }
                })
            }

            launch {
                binding.swipeRefreshLayout.setOnRefreshListener {
                    viewModel.fetchRepositoryList()
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }

            launch {
                binding.repoRecyclerView.addOnScrollListener(object : PaginationScrollListener(
                    LinearLayoutManager(requireContext())
                ) {
                    override fun isLastPage(): Boolean {
                        return isLastPage
                    }

                    override fun isLoading(): Boolean {
                        return isLoading
                    }

                    override fun loadMoreItems() {
                        isLoading = true
                        viewModel.fetchMoreRepositoryList()
                    }
                })
            }

            launch {
                viewModel.navigationActions.collect {
                    if (it == HomeNavigationAction.NavigateToDetailsAction) {
                        navigate(HomeFragmentDirections.actionHomeFragmentToDetailsFragment(viewModel.item))
                    }
                }
            }
        }
    }
}