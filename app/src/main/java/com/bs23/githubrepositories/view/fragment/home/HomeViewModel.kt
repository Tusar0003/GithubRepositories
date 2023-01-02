package com.bs23.githubrepositories.view.fragment.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bs23.githubrepositories.api.Result
import com.bs23.githubrepositories.api.Status
import com.bs23.githubrepositories.domain.LoadRepositoryListUseCase
import com.bs23.githubrepositories.model.Items
import com.bs23.githubrepositories.model.Repository
import com.bs23.githubrepositories.model.RepositoryPayload
import com.bs23.githubrepositories.utils.WhileViewSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val loadRepositoryListUseCase: LoadRepositoryListUseCase
) : ViewModel() {

    private val _message = Channel<String>(Channel.CONFLATED)
    val message = _message.receiveAsFlow()

    private val _navigationActions = Channel<HomeNavigationAction>(Channel.CONFLATED)
    val navigationActions = _navigationActions.receiveAsFlow()

    private val fetchRepository = MutableSharedFlow<RepositoryPayload>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val githubRepoResponse = fetchRepository.flatMapLatest {
        loadRepositoryListUseCase(it)
    }.stateIn(
        scope = viewModelScope,
        started = WhileViewSubscribed,
        initialValue = Result.nothing()
    )

    val repositoryList = MutableStateFlow<Repository>(
        Repository(
            totalCount = 0,
            items = emptyList()
        )
    )

    lateinit var item: Items

    init {
        viewModelScope.launch {
            fetchRepositoryList()
        }

        viewModelScope.launch {
            githubRepoResponse.collect {
                if (it.status == Status.SUCCESS) {
                    if (fetchRepository.replayCache.first().page > 0) {
                        val newItemList: List<Items> = it.data?.items!!.plus(repositoryList.replayCache.first().items)
                        repositoryList.tryEmit(
                            Repository(
                                totalCount = it.data.totalCount,
                                items = newItemList
                            )
                        )
                    } else {
                        repositoryList.tryEmit(it.data!!)
                    }
                    return@collect
                }

                if (it.status == Status.ERROR) {
                    _message.trySend(it.message.toString())
                }
            }
        }
    }

    fun fetchRepositoryList() {
        fetchRepository.tryEmit(
            RepositoryPayload(
                page = 0,
                perPage = 30,
                query = "Android"
            )
        )
    }

    fun fetchMoreRepositoryList() {
        fetchRepository.tryEmit(
            RepositoryPayload(
                page = fetchRepository.replayCache.first().page + 1,
                perPage = 10,
                query = "Android"
            )
        )
    }

    fun sortByStar() {
        val newRepoList = githubRepoResponse.replayCache.first().data?.items?.sortedBy {
            it.stargazersCount
        }

        repositoryList.tryEmit(
            Repository(
                totalCount = githubRepoResponse.replayCache.first().data!!.totalCount,
                items = newRepoList!!
            )
        )
    }

    fun sortByDate() {
        val newRepoList = githubRepoResponse.replayCache.first().data?.items?.sortedBy {
            it.updatedAt
        }

        repositoryList.tryEmit(
            Repository(
                totalCount = githubRepoResponse.replayCache.first().data!!.totalCount,
                items = newRepoList!!
            )
        )
    }

    fun onDetailsClick(item: Items) {
        this.item = item
        _navigationActions.trySend(HomeNavigationAction.NavigateToDetailsAction)
    }
}

sealed class HomeNavigationAction {
    object NavigateToDetailsAction : HomeNavigationAction()
}