package com.enterprise.core.domain.usecase

import com.enterprise.core.common.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Base use case enforcing coroutine dispatcher injection.
 * Allows tests to inject Dispatchers.Unconfined or TestDispatcher.
 */
abstract class UseCase<in Params, out T>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke(params: Params): Result<T> =
        withContext(dispatcher) { execute(params) }

    protected abstract suspend fun execute(params: Params): Result<T>
}

/** Use case with no parameters. */
abstract class NoParamsUseCase<out T>(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : UseCase<Unit, T>(dispatcher) {
    suspend operator fun invoke(): Result<T> = invoke(Unit)
}

/** Use case returning a Flow. */
abstract class FlowUseCase<in Params, out T>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(params: Params) = execute(params)
    protected abstract fun execute(params: Params): kotlinx.coroutines.flow.Flow<Result<T>>
}