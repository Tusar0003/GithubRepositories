package com.bs23.githubrepositories.service

import com.bs23.githubrepositories.api.ApiResponse
import com.bs23.githubrepositories.model.Repository
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("search/repositories")
    fun fetchRepositoryList(
        @Query("q") query: String,
    ) : Flow<ApiResponse<Repository>>
}