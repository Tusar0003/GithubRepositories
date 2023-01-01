package com.bs23.githubrepositories.network

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.bs23.githubrepositories.di.IoDispatcher
import com.bs23.githubrepositories.api.ApiEmptyResponse
import com.bs23.githubrepositories.api.ApiErrorResponse
import com.bs23.githubrepositories.api.ApiResponse
import com.bs23.githubrepositories.api.ApiSuccessResponse
import com.bs23.githubrepositories.api.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.withContext


@ExperimentalCoroutinesApi
abstract class NetworkResource<RequestType>(
    @IoDispatcher val dispatcher: CoroutineDispatcher
) {
    suspend fun asFlow(): Flow<Result<RequestType>> {
        return createCall().transformLatest {
            when (it) {
                is ApiSuccessResponse -> {
                    val data = processResponse(it)
                    withContext(dispatcher) {
                        saveCallResult(data)
                        emit(Result.success(data))
                    }
                }
                is ApiEmptyResponse -> {
                    emit(Result.success(null))
                }
                is ApiErrorResponse -> {
                    onFetchFailed()
                    emit(Result.error(it.errorMessage, null))
                }
            }
        }.onStart {
            emit(Result.loading(null))
        }
    }

    protected open fun onFetchFailed() {
    }

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    @WorkerThread
    protected open suspend fun saveCallResult(data: RequestType) {
    }

    @MainThread
    protected abstract suspend fun createCall(): Flow<ApiResponse<RequestType>>
}