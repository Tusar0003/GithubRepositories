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
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.withContext


@ExperimentalCoroutinesApi
abstract class NetworkBoundResource<ResultType, RequestType>(
    @IoDispatcher val dispatcher: CoroutineDispatcher
) {
    suspend fun asFlow(): Flow<Result<ResultType>> {
        return loadFromDb().transformLatest { dbValue ->
            if (shouldFetch(dbValue)) {
                emit(Result.loading(dbValue))
                createCall().collect { apiResponse ->
                    when (apiResponse) {
                        is ApiSuccessResponse -> withContext(dispatcher) {
                            saveCallResult(processResponse(apiResponse))
                            emitAll(loadFromDb().mapLatest { Result.success(it) })
                        }
                        is ApiEmptyResponse -> emit(Result.success(dbValue))
                        is ApiErrorResponse -> {
                            onFetchFailed()
                            emit(Result.error(apiResponse.errorMessage, dbValue))
                        }
                    }
                }

            } else {
                emit(Result.success(dbValue))
            }
        }
    }

    protected open fun onFetchFailed() {
    }

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    @WorkerThread
    protected abstract suspend fun saveCallResult(data: RequestType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): Flow<ResultType?>

    @MainThread
    protected abstract fun createCall(): Flow<ApiResponse<RequestType>>
}