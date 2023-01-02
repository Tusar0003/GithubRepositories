package com.bs23.githubrepositories.repository

import com.bs23.githubrepositories.db.GithubRepoDatabase
import com.bs23.githubrepositories.di.IoDispatcher
import com.bs23.githubrepositories.model.Items
import com.bs23.githubrepositories.model.Owner
import com.bs23.githubrepositories.model.Repository
import com.bs23.githubrepositories.network.OfflineNetworkResource
import com.bs23.githubrepositories.utils.ControlledRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@ExperimentalCoroutinesApi
class GithubRepoOfflineRepository @Inject constructor(
    @IoDispatcher val dispatcher: CoroutineDispatcher,
    private val database: GithubRepoDatabase,
) {
    private val controlledRunner = ControlledRunner<Flow<Repository?>>()

    suspend fun fetchRepositoryListOffline(): Flow<Repository?> {
        return controlledRunner.cancelPreviousThenRun {
            object : OfflineNetworkResource<Repository?>(dispatcher) {
                override suspend fun loadFromDb(): Flow<Repository?> {
                    val repositoryListOffline = database.repositoryDao.getRepositoryList()
                    val repositoryList = mutableListOf<Items>()

                    repositoryListOffline.first().forEach {
                        it?.let {
                            repositoryList.add(
                                Items(
                                    fullName = it.fullName,
                                    repoName = it.repoName,
                                    description = it.description,
                                    updatedAt = it.updatedAt,
                                    stargazersCount = it.stargazersCount,
                                    owner = Owner(
                                        avatarUrl = it.avatarUrl
                                    )
                                )
                            )
                        }
                    }

                    if (repositoryList.isNotEmpty()) {
                        return flow {
                            emit(
                                Repository(
                                    totalCount = repositoryListOffline.first()[0]!!.totalCount,
                                    items = repositoryList
                                )
                            )
                        }
                    } else {
                        return flow {
                            emit(
                                Repository(
                                    totalCount = 0,
                                    items = repositoryList
                                )
                            )
                        }
                    }
                }
            }.asFlow()
        }
    }
}
