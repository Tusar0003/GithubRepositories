package com.bs23.githubrepositories.network

import androidx.annotation.MainThread
import com.bs23.githubrepositories.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformLatest


@ExperimentalCoroutinesApi
abstract class OfflineNetworkResource<ResultType>(
    @IoDispatcher val dispatcher: CoroutineDispatcher
) {
    suspend fun asFlow(): Flow<ResultType?> {
        return loadFromDb().transformLatest { dbValue ->
            emit(dbValue)
        }
    }

    @MainThread
    protected abstract suspend fun loadFromDb(): Flow<ResultType?>
}
