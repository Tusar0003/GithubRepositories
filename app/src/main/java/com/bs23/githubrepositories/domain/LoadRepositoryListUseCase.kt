package com.bs23.githubrepositories.domain

import com.bs23.githubrepositories.api.FlowUseCase
import com.bs23.githubrepositories.di.IoDispatcher
import com.bs23.githubrepositories.model.Repository
import com.bs23.githubrepositories.model.RepositoryPayload
import com.bs23.githubrepositories.repository.GithubRepoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
open class LoadRepositoryListUseCase @Inject constructor(
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val repository: GithubRepoRepository
) : FlowUseCase<RepositoryPayload, Repository>(ioDispatcher) {

    override suspend fun execute(parameters: RepositoryPayload) = repository.fetchGithubRepoList(parameters)
}
