package com.bs23.githubrepositories.view.fragment.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bs23.githubrepositories.api.Result
import com.bs23.githubrepositories.api.Status
import com.bs23.githubrepositories.domain.LoadRepositoryListUseCase
import com.bs23.githubrepositories.utils.WhileViewSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val loadRepositoryListUseCase: LoadRepositoryListUseCase
) : ViewModel() {

    private val _message = Channel<String>(Channel.CONFLATED)
    val message = _message.receiveAsFlow()

    private val fetchRepository = MutableSharedFlow<String>(
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

    init {
        viewModelScope.launch {
            fetchRepositoryList()
        }

        viewModelScope.launch {
            githubRepoResponse.collect {
                if (it.status == Status.ERROR) {
                    _message.trySend(it.message.toString())
                }
            }
        }
    }

    fun fetchRepositoryList() {
        fetchRepository.tryEmit("Android")
    }
}