package com.studiomk.matool.domain.entities.shared

sealed class Result<out V, out E> {
    data class Success<V,E>(override val value: V) : Result<V, E>()
    data class Failure<V,E>(override val error: E) : Result<V, E>()

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    open val value: V?
        get() = when (this) {
            is Success -> this.value
            is Failure -> null
        }

    open val error: E?
        get() = when (this) {
            is Success -> null
            is Failure -> this.error
        }

    inline fun <R> map(transform: (V) -> R): Result<R, E> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> Failure(error)
    }

    inline fun <F> mapError(transform: (E) -> F): Result<V, F> = when (this) {
        is Success -> Success(value)
        is Failure -> Failure(transform(error))
    }
}