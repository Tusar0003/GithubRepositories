package com.bs23.githubrepositories.utils

import com.bs23.githubrepositories.api.ApiResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

@ExperimentalCoroutinesApi
class FlowCallAdapterFactory private constructor() : CallAdapter.Factory() {
    companion object {
        @JvmStatic
        @JvmName("create")
        operator fun invoke() = FlowCallAdapterFactory()
    }

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (Flow::class.java != getRawType(returnType)) {
            return null
        }
        if (returnType !is ParameterizedType) {
            throw IllegalStateException(
                "Flow  return type must be parameterized as Flow <Foo> or Deferred<out Foo>"
            )
        }
        val responseType = getParameterUpperBound(0, returnType)

        val rawDeferredType = getRawType(responseType)
        return if (rawDeferredType == ApiResponse::class.java) {
            if (responseType !is ParameterizedType) {
                throw IllegalStateException(
                    "Response must be parameterized as ApiResponse<Foo> or ApiResponse<out Foo>"
                )
            }
            FlowCallAdapter<Any>(getParameterUpperBound(0, responseType))
        } else {
            BodyCallAdapter<Any>(responseType)
        }
    }
}

/**
 * A Retrofit adapter that converts the Call into a LiveData of ApiResponse.
 * @param <R>
</R> */
@ExperimentalCoroutinesApi
internal class FlowCallAdapter<R>(
    private val responseType: Type
) : CallAdapter<R, Flow<ApiResponse<R>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): Flow<ApiResponse<R>> {
        return callbackFlow {
            call.enqueue(object : Callback<R> {
                override fun onResponse(call: Call<R>, response: Response<R>) {
                    if (!this@callbackFlow.isClosedForSend) trySend(ApiResponse.create(response)).isSuccess
                }

                override fun onFailure(call: Call<R>, throwable: Throwable) {
                    trySend(ApiResponse.create(throwable)).isSuccess
                }
            })

            awaitClose {
                call.cancel()
            }
        }
    }
}

@ExperimentalCoroutinesApi
private class BodyCallAdapter<T>(
    private val responseType: Type
) : CallAdapter<T, Flow<T>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<T>): Flow<T> {
        return callbackFlow {
            call.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (!this@callbackFlow.isClosedForSend) trySend(response.body()!!).isSuccess
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                }
            })

            awaitClose {
                call.cancel()
            }

        }
    }
}
