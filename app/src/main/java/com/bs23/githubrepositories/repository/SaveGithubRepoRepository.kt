package com.bs23.githubrepositories.repository

import com.bs23.githubrepositories.db.GithubRepoDatabase
import com.bs23.githubrepositories.db.entity.RepositoryOffline
import com.bs23.githubrepositories.di.IoDispatcher
import com.bs23.githubrepositories.model.Repository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@ExperimentalCoroutinesApi
class SaveGithubRepoRepository @Inject constructor(
    @IoDispatcher val dispatcher: CoroutineDispatcher,
    private val database: GithubRepoDatabase,
) {
    suspend fun saveForecastDetails(repository: Repository): Flow<Boolean> {
        val isInserted = MutableStateFlow<Boolean>(false)
        repository.items.forEach {
            database.repositoryDao.insert(
                RepositoryOffline(
                    totalCount = repository.totalCount,
                    repoName = it.repoName,
                    fullName = it.fullName,
                    stargazersCount = it.stargazersCount,
                    avatarUrl = it.owner.avatarUrl,
                )
            )
        }
        isInserted.tryEmit(true)
        return isInserted
    }
}
