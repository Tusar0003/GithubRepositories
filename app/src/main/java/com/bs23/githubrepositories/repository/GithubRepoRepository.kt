package com.bs23.githubrepositories.repository

import com.bs23.githubrepositories.api.ApiResponse
import com.bs23.githubrepositories.api.Result
import com.bs23.githubrepositories.di.IoDispatcher
import com.bs23.githubrepositories.model.Repository
import com.bs23.githubrepositories.model.RepositoryPayload
import com.bs23.githubrepositories.network.NetworkBoundResource
import com.bs23.githubrepositories.network.NetworkResource
import com.bs23.githubrepositories.service.ApiService
import com.bs23.githubrepositories.utils.ControlledRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@ExperimentalCoroutinesApi
class GithubRepoRepository @Inject constructor(
    @IoDispatcher val dispatcher: CoroutineDispatcher,
    val apiService: ApiService,
    val saveGithubRepoRepository: SaveGithubRepoRepository,
    val githubRepoOfflineRepository: GithubRepoOfflineRepository
) {
    private val controlledRunner = ControlledRunner<Flow<Result<Repository>>>()

    suspend fun fetchGithubRepoList(repositoryPayload: RepositoryPayload): Flow<Result<Repository>> {
        return controlledRunner.cancelPreviousThenRun {
            object : NetworkBoundResource<Repository, Repository>(dispatcher) {
                override suspend fun saveCallResult(data: Repository) {
                    saveGithubRepoRepository.saveForecastDetails(data)
                }

                override fun shouldFetch(data: Repository?): Boolean {
                    return true
                }

                override fun createCall(): Flow<ApiResponse<Repository>> {
                    return apiService.fetchRepositoryList(
                        query = repositoryPayload.query,
                        page = repositoryPayload.page,
                        perPage = repositoryPayload.perPage
                    )
                }

                override suspend fun loadFromDb(): Flow<Repository?> {
                    return githubRepoOfflineRepository.fetchRepositoryListOffline()
                }
            }.asFlow()
        }
    }

//    suspend fun fetchGithubRepoList(query: String): Flow<Result<Repository>> {
//        return controlledRunner.cancelPreviousThenRun {
//            object : NetworkResource<Repository>(dispatcher) {
//                override suspend fun createCall(): Flow<ApiResponse<Repository>> {
//                    return apiService.fetchRepositoryList(query)
//                }
//            }.asFlow()
//        }
//    }
}
