package com.bs23.githubrepositories.view.fragment.home

import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahoy.weatherapp.utils.layoutInflater
import com.bs23.githubrepositories.databinding.LayoutGithubRepoBinding
import com.bs23.githubrepositories.model.Items
import com.bs23.githubrepositories.utils.clearDecorations
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class GithubRepoAdapter(val viewModel: HomeViewModel) :
    ListAdapter<Items, GithubRepoViewHolder>(GithubRepoDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GithubRepoViewHolder {
        return GithubRepoViewHolder(
            LayoutGithubRepoBinding.inflate(parent.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GithubRepoViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }
}

@ExperimentalCoroutinesApi
class GithubRepoViewHolder(private val binding: LayoutGithubRepoBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: HomeViewModel, data: Items) {
        binding.viewModel = viewModel
        binding.item = data
        binding.executePendingBindings()
    }
}

private class GithubRepoDiffCallback : DiffUtil.ItemCallback<Items>() {
    override fun areItemsTheSame(oldItem: Items, newItem: Items) =
        oldItem.repoName == newItem.repoName

    override fun areContentsTheSame(oldItem: Items, newItem: Items) =
        oldItem == newItem
}

@ExperimentalCoroutinesApi
@BindingAdapter(value = ["homeViewModel", "githubRepoList"], requireAll = true)
fun RecyclerView.bindGithubRepoAdapter(viewModel: HomeViewModel, data: List<Items>?) {
    if (adapter == null) {
        adapter = GithubRepoAdapter(viewModel)
    }

    val value = data ?: emptyList()
    val githubRepoAdapter = adapter as? GithubRepoAdapter
    githubRepoAdapter?.submitList(value)
    clearDecorations()
}
